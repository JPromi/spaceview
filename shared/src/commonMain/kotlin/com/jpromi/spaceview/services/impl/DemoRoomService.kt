package com.jpromi.spaceview.services.impl

import com.jpromi.spaceview.enums.SlotStatus
import com.jpromi.spaceview.models.Event
import com.jpromi.spaceview.models.Room
import com.jpromi.spaceview.models.RoomUse
import com.jpromi.spaceview.models.Slot
import com.jpromi.spaceview.network.ApiResult
import com.jpromi.spaceview.services.RoomService
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

class DemoRoomService : RoomService {
    private val rooms = listOf(
        Room(
            id = "demo-room-main",
            name = "Demo Meetingraum",
            description = "Demo room for local testing.",
            email = "demo-room@example.com",
            capacity = 8,
            roomNumber = "D-101",
            roomType = "meeting",
            facilities = listOf("Display", "Whiteboard", "Video Call"),
            location = "Demo Floor",
            active = true,
        ),
        Room(
            id = "demo-room-focus",
            name = "Demo Fokusraum",
            description = "Small demo room for focused work.",
            email = "focus-room@example.com",
            capacity = 4,
            roomNumber = "D-102",
            roomType = "focus",
            facilities = listOf("Screen", "Desk"),
            location = "Demo Floor",
            active = true,
        ),
    )

    override fun configure(
        serverUrl: String,
        accessToken: String,
    ) {
        // Demo data does not need external credentials.
    }

    override suspend fun checkCredentials(): ApiResult<String> =
        ApiResult.Success("")

    override suspend fun getRooms(): ApiResult<List<Room>> =
        ApiResult.Success(rooms)

    override suspend fun getRoomById(roomId: String): ApiResult<Room?> =
        ApiResult.Success(rooms.firstOrNull { it.id == roomId })

    override suspend fun getRoomEvents(roomId: String): ApiResult<List<Event>> =
        ApiResult.Success(createEvents(roomId, LocalDate(2026, 8, 20)))

    override suspend fun getRoomSlots(
        roomId: String,
        date: LocalDate,
    ): ApiResult<List<Slot>> =
        ApiResult.Success(createRoomUse(roomId, date).slots)

    override suspend fun getRoomUse(
        roomId: String,
        date: LocalDate,
    ): ApiResult<RoomUse> =
        ApiResult.Success(createRoomUse(roomId, date))

    private fun createRoomUse(roomId: String, date: LocalDate): RoomUse {
        val events = createEvents(roomId, date)

        return RoomUse(
            date = date,
            slots = listOf(
                Slot(
                    start = LocalDateTime(date, LocalTime(8, 0)),
                    end = LocalDateTime(date, LocalTime(9, 30)),
                    status = SlotStatus.FREE,
                ),
                Slot(
                    start = LocalDateTime(date, LocalTime(9, 30)),
                    end = LocalDateTime(date, LocalTime(10, 30)),
                    event = events[0],
                    status = SlotStatus.BOOKED,
                ),
                Slot(
                    start = LocalDateTime(date, LocalTime(10, 30)),
                    end = LocalDateTime(date, LocalTime(11, 0)),
                    status = SlotStatus.FREE,
                ),
                Slot(
                    start = LocalDateTime(date, LocalTime(11, 0)),
                    end = LocalDateTime(date, LocalTime(12, 0)),
                    event = events[1],
                    status = SlotStatus.BOOKED,
                ),
                Slot(
                    start = LocalDateTime(date, LocalTime(12, 0)),
                    end = LocalDateTime(date, LocalTime(14, 0)),
                    status = SlotStatus.FREE,
                ),
                Slot(
                    start = LocalDateTime(date, LocalTime(14, 0)),
                    end = LocalDateTime(date, LocalTime(15, 0)),
                    event = events[2],
                    status = SlotStatus.BOOKED,
                ),
                Slot(
                    start = LocalDateTime(date, LocalTime(15, 0)),
                    end = LocalDateTime(date, LocalTime(18, 0)),
                    status = SlotStatus.FREE,
                ),
            ),
            currentEvent = null,
            futureEvents = events,
            pastEvents = emptyList(),
        )
    }

    private fun createEvents(roomId: String, date: LocalDate): List<Event> =
        listOf(
            Event(
                id = "demo-event-product-sync",
                title = "Produkt Sync",
                start = LocalDateTime(date, LocalTime(9, 30)).toString(),
                end = LocalDateTime(date, LocalTime(10, 30)).toString(),
                organizer = "Demo Team",
                status = "confirmed",
                roomId = roomId,
            ),
            Event(
                id = "demo-event-design-review",
                title = "Design Review",
                start = LocalDateTime(date, LocalTime(11, 0)).toString(),
                end = LocalDateTime(date, LocalTime(12, 0)).toString(),
                organizer = "Demo Team",
                status = "confirmed",
                roomId = roomId,
            ),
            Event(
                id = "demo-event-retro",
                title = "Team Retro",
                start = LocalDateTime(date, LocalTime(14, 0)).toString(),
                end = LocalDateTime(date, LocalTime(15, 0)).toString(),
                organizer = "Demo Team",
                status = "confirmed",
                roomId = roomId,
            ),
        )
}
