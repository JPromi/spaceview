package com.jpromi.spaceview.dtos.roomvox

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RVRoomAvailabilityDTO(
    var room: RVAvailabilityRoomDTO,
    var date: String,
    var availabilityRules: JsonElement? = null,
    var slots: List<RVAvailabilitySlotDTO>,
)

@Serializable
data class RVAvailabilityRoomDTO(
    var id: String,
    var name: String,
)

@Serializable
data class RVAvailabilitySlotDTO(
    var start: String,
    var end: String,
    var status: RVSlotStatusDTO,
    var title: String? = null,
)
