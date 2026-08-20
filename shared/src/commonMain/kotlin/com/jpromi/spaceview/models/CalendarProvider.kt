package com.jpromi.spaceview.models

import com.jpromi.spaceview.enums.CalendarProviderENUM
import kotlinx.serialization.Serializable

@Serializable
data class CalendarProvider(
    val id: CalendarProviderENUM,
    val name: String
)
