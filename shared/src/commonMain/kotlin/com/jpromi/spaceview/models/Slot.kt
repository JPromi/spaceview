package com.jpromi.spaceview.models

import com.jpromi.spaceview.enums.SlotStatus
import kotlinx.serialization.Serializable

@Serializable
data class Slot (
    var start: String,
    var end: String,
    var event: Event,
    var status: SlotStatus,
)