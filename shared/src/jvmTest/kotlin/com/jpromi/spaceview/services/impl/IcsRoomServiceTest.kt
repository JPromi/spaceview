package com.jpromi.spaceview.services.impl

import com.jpromi.spaceview.enums.SlotStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IcsRoomServiceTest {
    private val service = IcsRoomService()
    private val date = LocalDate(2026, 9, 7)
    private val now = LocalDateTime(2026, 9, 7, 10, 0)

    private fun event(id: String, start: String, end: String, extra: String = "") = """
        BEGIN:VEVENT
        UID:$id
        DTSTART:$start
        DTEND:$end
        $extra
        END:VEVENT
    """.trimIndent()

    @Test
    fun emptyCalendarHasOneFreeSlot() {
        val use = service.buildIcsRoomUse("BEGIN:VCALENDAR\nEND:VCALENDAR", "room", date, now)
        assertEquals(1, use.slots.size)
        assertEquals(SlotStatus.FREE, use.slots.single().status)
        assertEquals(LocalDateTime(2026, 9, 7, 0, 0), use.slots.single().start)
        assertEquals(LocalDateTime(2026, 9, 7, 23, 59, 59), use.slots.single().end)
        assertNull(use.currentEvent)
    }

    @Test
    fun classifiesAndSortsOnlyEventsOnRequestedDay() {
        val calendar = listOf(
            event("future", "20260907T120000", "20260907T130000"),
            event("past", "20260907T080000", "20260907T100000"),
            event("current", "20260907T100000", "20260907T110000"),
            event("tomorrow", "20260908T100000", "20260908T110000"),
            event("cancelled", "20260907T140000", "20260907T150000", "STATUS:CANCELLED"),
        ).joinToString("\n")
        val use = service.buildIcsRoomUse(calendar, "room", date, now)
        assertEquals("current", use.currentEvent?.id)
        assertEquals(listOf("past"), use.pastEvents.map { it.id })
        assertEquals(listOf("future"), use.futureEvents.map { it.id })
        assertEquals("room", use.currentEvent?.roomId)
        assertEquals(listOf(SlotStatus.FREE, SlotStatus.BOOKED, SlotStatus.BOOKED, SlotStatus.FREE, SlotStatus.BOOKED, SlotStatus.FREE), use.slots.map { it.status })
    }

    @Test
    fun overlappingAndOvernightEventsProduceContinuousSlots() {
        val calendar = listOf(
            event("overnight", "20260906T220000", "20260907T090000"),
            event("overlap", "20260907T080000", "20260907T110000"),
            event("contained", "20260907T083000", "20260907T084500"),
        ).joinToString("\n")
        val use = service.buildIcsRoomUse(calendar, "room", date, now)
        assertEquals(3, use.slots.size)
        assertEquals(LocalDateTime(2026, 9, 7, 0, 0), use.slots.first().start)
        assertTrue(use.slots.all { it.end > it.start })
        assertTrue(use.slots.zipWithNext().all { (a, b) -> a.end == b.start })
    }

    @Test
    fun allDayEventEndsAtExclusiveDate() {
        val calendar = event("all-day", "20260907", "20260908")
        val use = service.buildIcsRoomUse(calendar, "room", date, now)
        assertEquals(SlotStatus.BOOKED, use.slots.single().status)
        val nextDay = service.buildIcsRoomUse(calendar, "room", LocalDate(2026, 9, 8), now)
        assertEquals(SlotStatus.FREE, nextDay.slots.single().status)
    }
}
