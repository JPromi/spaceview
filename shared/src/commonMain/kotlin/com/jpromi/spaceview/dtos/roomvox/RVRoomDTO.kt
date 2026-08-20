package com.jpromi.spaceview.dtos.roomvox

import com.jpromi.spaceview.models.Room
import kotlinx.serialization.Serializable

@Serializable
data class RVRoomDTO(
    var id: String,
    var name: String,
    var email: String,
    var capacity: Int,
    var roomNumber: String,
    var roomType: String,
    var facilities: List<String>,
    var description: String,
    var responsibleContact: String,
    var location: String,
    var autoAccept: Boolean,
    var active: Boolean,
)

public fun RVRoomDTO.toRoom(): Room {
    return Room(
        id = id,
        name = name,
        email = email,
        capacity = capacity,
        roomNumber = roomNumber,
        roomType = roomType,
        facilities = facilities,
        description = description,
        location = location,
        active = active,
    )
}