package com.jpromi.spaceview.services

import com.jpromi.spaceview.models.Event
import com.jpromi.spaceview.models.Room
import com.jpromi.spaceview.models.RoomUse
import com.jpromi.spaceview.models.Slot
import com.jpromi.spaceview.network.ApiResult
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface RoomService {
    fun configure(serverUrl: String = "", accessToken: String = "")

    suspend fun checkCredentials(): ApiResult<String>
    suspend fun getRooms(): ApiResult<List<Room>>
    suspend fun getRoomById(roomId: String): ApiResult<Room?>
    suspend fun getRoomEvents(roomId: String): ApiResult<List<Event>>
    suspend fun getRoomSlots(
        roomId: String,
        date: LocalDate = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    ): ApiResult<List<Slot>>
    suspend fun getRoomUse(
        roomId: String,
        date: LocalDate = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    ): ApiResult<RoomUse>
}
