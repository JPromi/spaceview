package com.jpromi.spaceview.dtos.roomvox

import com.jpromi.spaceview.models.Event
import kotlinx.serialization.Serializable

@Serializable
data class RVRoomBookingDTO(
    var uid: String,
    var title: String,
    var start: String,
    var end: String,
    var organizer: String,
    var status: String,
    var room: RVBookingRoomDTO,
)

@Serializable
data class RVBookingRoomDTO(
    var id: String,
    var name: String,
)

public fun RVRoomBookingDTO.toEvent(): Event {
    return Event(
        id = uid,
        title = title,
        start = start,
        end = end,
        organizer = organizer,
        status = status,
        roomId = room.id
    )
}