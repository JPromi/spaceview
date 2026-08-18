package com.jpromi.spaceview.models

import kotlinx.serialization.Serializable

@Serializable
data class RoomStatus(
    var room: Room,
    var status: String,
    var currentBooking: CurrentRoomBooking? = null,
    var nextBooking: CurrentRoomBooking? = null,
    var freeUntil: String? = null,
    var todayBookings: List<TodayRoomBooking> = emptyList(),
)

@Serializable
data class CurrentRoomBooking(
    var title: String,
    var organizer: String,
    var start: String,
    var end: String,
    var minutesRemaining: Int,
)

@Serializable
data class TodayRoomBooking(
    var title: String,
    var start: String,
    var end: String,
    var status: String,
)
