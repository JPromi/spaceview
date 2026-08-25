package com.jpromi.spaceview.dtos.roomvox

import kotlinx.serialization.Serializable

@Serializable
data class RVRoomStatusDTO(
    var room: RVRoomDTO,
    var status: String,
    var currentBooking: RVCurrentRoomBookingDTO? = null,
    var nextBooking: RVCurrentRoomBookingDTO? = null,
    var freeUntil: String? = null,
    var todayBookings: List<RVTodayRoomBookingDTO> = emptyList(),
)

@Serializable
data class RVCurrentRoomBookingDTO(
    var title: String,
    var organizer: String,
    var start: String,
    var end: String,
    var minutesRemaining: Int,
)

@Serializable
data class RVTodayRoomBookingDTO(
    var title: String,
    var start: String,
    var end: String,
    var status: String,
)
