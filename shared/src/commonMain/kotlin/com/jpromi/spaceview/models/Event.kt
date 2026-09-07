package com.jpromi.spaceview.models

import kotlinx.serialization.Serializable

@Serializable
data class Event (
    var id: String,
    var title: String? = null,
    var description: String? = null,
    var start: String,
    var end: String,
    var organizer: String? = null,
    var status: String? = null,
    var roomId: String? = null,
)