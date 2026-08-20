package com.jpromi.spaceview.models

import com.jpromi.spaceview.enums.SlotStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Slot (
    var start: LocalDateTime,
    var end: LocalDateTime,
    var event: Event? = null,
    var status: SlotStatus,
)
