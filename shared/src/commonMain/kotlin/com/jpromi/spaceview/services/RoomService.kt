package com.jpromi.spaceview.services

import com.jpromi.spaceview.AppSettings
import com.jpromi.spaceview.dtos.roomvox.RVRoomDTO
import com.jpromi.spaceview.models.Room


class RoomService(
    private val appSettings: AppSettings = AppSettings(),
) {
    // ToDo:
    // - getRoom
    // - getRooms
    // - getSlots
    // - getEvents
    // - addEvent
    // - deleteEvent

    fun getRooms(): List<Room> {
        return emptyList()
    }
}