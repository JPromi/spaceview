package com.jpromi.spaceview.services.impl

import com.jpromi.spaceview.CalendarSettings
import com.jpromi.spaceview.dtos.roomvox.RVRoomBookingDTO
import com.jpromi.spaceview.dtos.roomvox.RVRoomDTO
import com.jpromi.spaceview.dtos.roomvox.toEvent
import com.jpromi.spaceview.dtos.roomvox.toRoom
import com.jpromi.spaceview.enums.SlotStatus
import com.jpromi.spaceview.models.Event
import com.jpromi.spaceview.models.Room
import com.jpromi.spaceview.models.RoomUse
import com.jpromi.spaceview.models.Slot
import com.jpromi.spaceview.network.ApiResult
import com.jpromi.spaceview.network.executeRequest
import com.jpromi.spaceview.network.toHttpBaseUrl
import com.jpromi.spaceview.services.RoomService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.jpromi.spaceview.dtos.roomvox.toRoomVoxLocalDateTimeOrNull

class RoomVoxRoomService(
    private val calendarSettings: CalendarSettings = CalendarSettings(),
) : RoomService {
    private var configuredServerUrl: String? = null
    private var configuredAccessToken: String? = null

    private val effectiveServerUrl: String
        get() = configuredServerUrl ?: calendarSettings.roomVoxServerUrl

    private val effectiveAccessToken: String
        get() = configuredAccessToken ?: calendarSettings.roomVoxAccessToken

    val baseUrl: String
        get() = effectiveServerUrl.toHttpBaseUrl() + "/apps/roomvox/api/v1"

    override fun configure(
        serverUrl: String,
        accessToken: String,
    ) {
        configuredServerUrl = serverUrl
        configuredAccessToken = accessToken
    }

    // check connection
    override suspend fun checkCredentials(): ApiResult<String> = executeRoomVoxRequest { client ->
        client.get("${baseUrl}/rooms") {
            addAuthorizationHeader()
        }
            .body<List<RVRoomDTO>>()
        ""
    }

    // get Rooms
    override suspend fun getRooms(): ApiResult<List<Room>> = executeRoomVoxRequest { client ->
        client.get("$baseUrl/rooms") {
            addAuthorizationHeader()
        }
            .body<List<RVRoomDTO>>()
            .map { it.toRoom() }
    }

    // get Room by ID
    override suspend fun getRoomById(roomId: String): ApiResult<Room?> = executeRoomVoxRequest { client ->
        client.get("$baseUrl/rooms/$roomId") {
            addAuthorizationHeader()
        }
            .body<RVRoomDTO>()
            .toRoom()
    }

    // get Events from Room
    // ToDo: Implement date range
    override suspend fun getRoomEvents(roomId: String): ApiResult<List<Event>> = executeRoomVoxRequest { client ->
        client.get("$baseUrl/rooms/$roomId/bookings") {
            addAuthorizationHeader()
        }
            .body<List<RVRoomBookingDTO>>()
            .map { it.toEvent() }
    }

    // get Room Slots
    override suspend fun getRoomSlots(roomId: String, date: LocalDate): ApiResult<List<Slot>> = executeRoomVoxRequest { client ->
        val from = LocalDateTime(date, LocalTime(0, 0))
        val to = LocalDateTime(date, LocalTime(23, 59, 59))
        client.get("$baseUrl/rooms/$roomId/bookings?from=$from&to=$to") {
            addAuthorizationHeader()
        }
            .body<List<RVRoomBookingDTO>>()
            .let { bookings -> generateSlotsFromBookings(bookings, date) }
    }

    override fun getLogoUrl(): String {
        return "${effectiveServerUrl.toHttpBaseUrl()}/apps/theming/image/logo"
    }
    // get Room use for date
    override suspend fun getRoomUse(roomId: String, date: LocalDate): ApiResult<RoomUse> = executeRoomVoxRequest { client ->
        val from = LocalDateTime(date, LocalTime(0, 0))
        val to = LocalDateTime(date, LocalTime(23, 59, 59))
        client.get("$baseUrl/rooms/$roomId/bookings?from=$from&to=$to") {
            addAuthorizationHeader()
        }
            .body<List<RVRoomBookingDTO>>()
            .let { bookings ->
                val now = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                val eventsWithTime = bookings
                    .mapNotNull { booking ->
                        val start = booking.start.toRoomVoxLocalDateTimeOrNull()
                        val end = booking.end.toRoomVoxLocalDateTimeOrNull()

                        if (start == null || end == null) {
                            null
                        } else {
                            EventTime(
                                event = booking.toEvent(),
                                start = start,
                                end = end,
                            )
                        }
                    }
                    .filter { it.end > it.start }
                    .sortedBy { it.start }

                RoomUse(
                    date = date,
                    slots = generateSlotsFromBookings(bookings, date),
                    currentEvent = eventsWithTime
                        .firstOrNull { it.start <= now && now < it.end }
                        ?.event,
                    futureEvents = eventsWithTime
                        .filter { it.start > now }
                        .map { it.event },
                    pastEvents = eventsWithTime
                        .filter { it.end <= now }
                        .map { it.event },
                )
            }
    }

    // generate slots from events
    private fun generateSlotsFromBookings(bookings: List<RVRoomBookingDTO>, date: LocalDate): List<Slot> {
        val dayStart = LocalDateTime(date, LocalTime(0, 0))
        val dayEnd = LocalDateTime(date, LocalTime(23, 59, 59))

        if (bookings.isEmpty()) {
            return listOf(
                Slot(
                    start = dayStart,
                    end = dayEnd,
                    status = SlotStatus.FREE,
                )
            )
        }

        val slots = mutableListOf<Slot>()
        val sortedBookings = bookings
            .mapNotNull { booking ->
                val bookingStart = booking.start.toRoomVoxLocalDateTimeOrNull()
                val bookingEnd = booking.end.toRoomVoxLocalDateTimeOrNull()

                if (bookingStart == null || bookingEnd == null) {
                    null
                } else {
                    BookingTime(
                        booking = booking,
                        start = bookingStart,
                        end = bookingEnd,
                    )
                }
            }
            .filter { it.end > dayStart && it.start < dayEnd }
            .filter { it.end > it.start }
            .sortedBy { it.start }

        if (sortedBookings.isEmpty()) {
            return listOf(
                Slot(
                    start = dayStart,
                    end = dayEnd,
                    status = SlotStatus.FREE,
                )
            )
        }

        var currentTime = dayStart

        sortedBookings.forEach { bookingTime ->
            val bookingStart = maxOf(bookingTime.start, dayStart)
            val bookingEnd = minOf(bookingTime.end, dayEnd)

            if (bookingEnd <= currentTime) {
                return@forEach
            }

            // Free time before booking
            if (currentTime < bookingStart) {
                slots.add(
                    Slot(
                        start = currentTime,
                        end = bookingStart,
                        status = SlotStatus.FREE,
                    )
                )
            }

            // Booking
            slots.add(
                Slot(
                    start = maxOf(bookingStart, currentTime),
                    end = bookingEnd,
                    event = bookingTime.booking.toEvent(),
                    status = SlotStatus.BOOKED,
                )
            )

            if (bookingEnd > currentTime) {
                currentTime = bookingEnd
            }
        }

        // fill last slot
        if (currentTime < dayEnd) {
            slots.add(
                Slot(
                    start = currentTime,
                    end = dayEnd,
                    status = SlotStatus.FREE,
                )
            )
        }

        return slots
    }

    private data class BookingTime(
        val booking: RVRoomBookingDTO,
        val start: LocalDateTime,
        val end: LocalDateTime,
    )

    private data class EventTime(
        val event: Event,
        val start: LocalDateTime,
        val end: LocalDateTime,
    )

    // execute request
    private suspend fun <T> executeRoomVoxRequest(request: suspend (HttpClient) -> T): ApiResult<T> =
        executeRequest(
            invalidRequestMessage = "Bitte Server-URL eingeben.",
            isRequestValid = { effectiveServerUrl.isNotBlank() },
            request = request,
        )

    // Authorization logik
    private fun HttpRequestBuilder.addAuthorizationHeader() {
        if (effectiveAccessToken.isNotBlank()) {
            bearerAuth(effectiveAccessToken.trim())
        }
    }
}
