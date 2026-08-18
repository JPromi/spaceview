package com.jpromi.spaceview.models

import kotlinx.serialization.Serializable

@Serializable
data class Room(
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