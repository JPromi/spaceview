package com.jpromi.spaceview.services.impl

import com.jpromi.spaceview.services.RoomService
import com.jpromi.spaceview.CalendarSettings
import com.jpromi.spaceview.models.Event
import com.jpromi.spaceview.models.Room
import com.jpromi.spaceview.models.RoomUse
import com.jpromi.spaceview.models.Slot
import com.jpromi.spaceview.network.ApiResult
import com.jpromi.spaceview.network.executeRequest
import com.jpromi.spaceview.enums.SlotStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class IcsRoomService(
    private val calendarSettings: CalendarSettings = CalendarSettings(),
) : RoomService {

    private var configurationUrl: String? = null

    private val effectiveConfigurationUrl: String
        get() = configurationUrl ?: calendarSettings.icsUrl

    val baseUrl: String
        get() = effectiveConfigurationUrl

    override fun configure(
        serverUrl: String,
        accessToken: String,
    ) {
        configurationUrl = serverUrl
    }

    override suspend fun checkCredentials(): ApiResult<String> = executeIcsRequest { client ->
        val ics = client.get(baseUrl).body<String>()

        require(ics.contains("BEGIN:VCALENDAR")) {
            "Response is not a valid ICS calendar"
        }

        ics
    }

    override suspend fun getRoomEvents(roomId: String): ApiResult<List<Event>> = executeIcsRequest { client ->
        val icsContent = client.get(baseUrl).body<String>()

        parseIcsEvents(icsContent)
    }

    override suspend fun getRoomSlots(roomId: String, date: LocalDate): ApiResult<List<Slot>> =
        executeIcsRequest { client ->
            buildIcsRoomUse(client.get(baseUrl).body<String>(), roomId, date).slots
        }

    override suspend fun getRoomUse(roomId: String, date: LocalDate): ApiResult<RoomUse> =
        executeIcsRequest { client ->
            buildIcsRoomUse(client.get(baseUrl).body<String>(), roomId, date)
        }

    internal fun buildIcsRoomUse(
        icsContent: String,
        roomId: String,
        date: LocalDate,
        now: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    ): RoomUse {
        val dayStart = LocalDateTime(date, LocalTime(0, 0))
        val dayEnd = LocalDateTime(date, LocalTime(23, 59, 59))
        val events = parseIcsEvents(icsContent)
            .filter { it.status != "CANCELLED" }
            .map { event ->
                EventTime(event.copy(roomId = roomId), LocalDateTime.parse(event.start), LocalDateTime.parse(event.end))
            }
            .filter { it.end > it.start && it.end > dayStart && it.start < dayEnd }
            .sortedBy { it.start }

        val slots = mutableListOf<Slot>()
        var cursor = dayStart
        for (event in events) {
            val start = maxOf(event.start, dayStart)
            val end = minOf(event.end, dayEnd)
            if (end <= cursor) continue
            if (cursor < start) {
                slots += Slot(start = cursor, end = start, status = SlotStatus.FREE)
            }
            slots += Slot(start = maxOf(cursor, start), end = end, event = event.event, status = SlotStatus.BOOKED)
            cursor = end
        }
        if (cursor < dayEnd) {
            slots += Slot(start = cursor, end = dayEnd, status = SlotStatus.FREE)
        }
        return RoomUse(
            date = date,
            slots = slots,
            currentEvent = events.firstOrNull { it.start <= now && now < it.end }?.event,
            futureEvents = events.filter { it.start > now }.map { it.event },
            pastEvents = events.filter { it.end <= now }.map { it.event },
        )
    }

    private data class EventTime(val event: Event, val start: LocalDateTime, val end: LocalDateTime)

    // ToDo: Move this to an external mapping file
    private companion object {
        val windowsTimeZoneMappings = mapOf(
            "W. Europe Standard Time" to "Europe/Berlin",
            "Central Europe Standard Time" to "Europe/Budapest",
            "Romance Standard Time" to "Europe/Paris",
            "GMT Standard Time" to "Europe/London",
            "UTC" to "UTC",
        )
    }

    // Not supported for ICS
    override suspend fun getRooms(): ApiResult<List<Room>> {
        return ApiResult.InvalidRequest("Not supported for ICS")
    }

    override suspend fun getRoomById(roomId: String): ApiResult<Room?> {
        return ApiResult.InvalidRequest("Not supported for ICS")
    }

    // execute request
    private suspend fun <T> executeIcsRequest(request: suspend (HttpClient) -> T): ApiResult<T> =
        executeRequest(
            invalidRequestMessage = "Bitte Server-URL eingeben.",
            isRequestValid = { effectiveConfigurationUrl.isNotBlank() },
            request = request,
        )

    // ICS Formater
    private fun parseIcsEvents(icsContent: String): List<Event> {
        val unfolded = icsContent
            .replace("\r\n", "\n")
            .lines()
            .fold(mutableListOf<String>()) { result, line ->
                if ((line.startsWith(" ") || line.startsWith("\t")) && result.isNotEmpty()) {
                    result[result.lastIndex] += line.drop(1)
                } else {
                    result += line
                }
                result
            }

        val events = mutableListOf<Event>()
        var current: MutableMap<String, String>? = null

        for (line in unfolded) {
            when (line) {
                "BEGIN:VEVENT" -> current = mutableMapOf()
                "END:VEVENT" -> {
                    current?.let {
                        events += Event(
                            id = requireNotNull(it["UID"]) { "ICS event is missing UID" },
                            title = it["SUMMARY"] ?: "",
                            description = it["DESCRIPTION"],
                            // location = it["LOCATION"],
                            start = parseIcsDateTime(requireNotNull(it["DTSTART"]) { "ICS event is missing DTSTART" }, it["DTSTART;TZID"]).toString(),
                            end = parseIcsDateTime(requireNotNull(it["DTEND"]) { "ICS event is missing DTEND" }, it["DTEND;TZID"]).toString(),
                            status = it["STATUS"],
                            organizer = it["ORGANIZER"],
                        )
                    }
                    current = null
                }

                else -> {
                    val properties = current
                    if (properties != null) {
                        val separator = line.indexOf(':')
                        if (separator > 0) {
                            val rawKey = line.substring(0, separator)
                            val value = line.substring(separator + 1)
                            // z.B. DTSTART;TZID=Europe/Vienna
                            val key = rawKey.substringBefore(';')
                            properties[key] = value
                            rawKey.split(';').drop(1).firstOrNull { it.startsWith("TZID=") }
                                ?.substringAfter('=')?.trim('"')?.let { properties["$key;TZID"] = it }
                        }
                    }
                }
            }
        }

        return events
    }

    fun parseIcsDateTime(value: String): LocalDateTime = parseIcsDateTime(value, null)

    private fun parseIcsDateTime(value: String, timeZoneId: String?): LocalDateTime {
        val date = LocalDate(value.substring(0, 4).toInt(), value.substring(4, 6).toInt(), value.substring(6, 8).toInt())
        if (value.length == 8) return LocalDateTime(date, LocalTime(0, 0))
        val local = LocalDateTime(date, LocalTime(
            value.substring(9, 11).toInt(),
            value.substring(11, 13).toInt(),
            value.substring(13, 15).toInt(),
        ))
        val sourceZone = when {
            value.endsWith("Z") -> TimeZone.UTC
            timeZoneId != null -> TimeZone.of(windowsTimeZoneMappings[timeZoneId] ?: timeZoneId)
            else -> return local
        }
        return local.toInstant(sourceZone).toLocalDateTime(TimeZone.currentSystemDefault())
    }
}
