package com.jpromi.spaceview.models

import kotlinx.serialization.Serializable

@Serializable
data class Room(
    var id: String,
    var name: String,
    var description: String?,
    var email: String?,
    var capacity: Int?,
    var roomNumber: String?,
    var roomType: String?,
    var facilities: List<String>,
    var location: String?,
    var active: Boolean,
    // ToDo: Rules (Opening times...)
)
