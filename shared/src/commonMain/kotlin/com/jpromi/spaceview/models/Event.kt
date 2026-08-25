package com.jpromi.spaceview.models

import kotlinx.serialization.Serializable

@Serializable
data class Event (
    var id: String,
    var title: String,
    var start: String,
    var end: String,
    var organizer: String,
    var status: String,
    var roomId: String? = null,
)