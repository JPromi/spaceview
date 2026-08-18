package com.jpromi.spaceview.models

import com.jpromi.spaceview.enums.SlotStatus
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RoomAvailability(
    var room: AvailabilityRoom,
    var date: String,
    var availabilityRules: JsonElement? = null,
    var slots: List<AvailabilitySlot>,
)

@Serializable
data class AvailabilityRoom(
    var id: String,
    var name: String,
)

@Serializable
data class AvailabilitySlot(
    var start: String,
    var end: String,
    var status: SlotStatus,
    var title: String? = null,
)
