package com.jpromi.spaceview.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RoomBooking(
    var uid: String,
    var title: String,
    var start: String,
    var end: String,
    var organizer: String,
    var status: String,
    var room: BookingRoom,
)

@Serializable
data class BookingRoom(
    var id: String,
    var name: String,
)
