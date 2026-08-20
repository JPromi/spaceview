package com.jpromi.spaceview.services

import com.jpromi.spaceview.dtos.roomvox.RVRoomAvailabilityDTO
import com.jpromi.spaceview.dtos.roomvox.RVRoomDTO
import com.jpromi.spaceview.dtos.roomvox.RVRoomStatusDTO
import com.jpromi.spaceview.models.Event
import com.jpromi.spaceview.models.Room
import com.jpromi.spaceview.models.Slot
import com.jpromi.spaceview.network.ApiResult
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface RoomService {
    suspend fun getRooms(): ApiResult<List<Room>>
    suspend fun getRoomById(roomId: String): ApiResult<Room?>
    suspend fun getRoomEvents(roomId: String): ApiResult<List<Event>>
    suspend fun getRoomSlots(
        roomId: String,
        date: LocalDate = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    ): ApiResult<List<Slot>>
}
