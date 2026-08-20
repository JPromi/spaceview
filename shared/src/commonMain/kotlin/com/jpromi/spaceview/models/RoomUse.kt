package com.jpromi.spaceview.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class RoomUse(
    val date: LocalDate,
    val slots: List<Slot>,
    val currentEvent: Event? = null,
    val futureEvents: List<Event> = emptyList(),
    val pastEvents: List<Event> = emptyList()
)
