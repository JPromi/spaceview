package com.jpromi.spaceview.dtos.roomvox

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
