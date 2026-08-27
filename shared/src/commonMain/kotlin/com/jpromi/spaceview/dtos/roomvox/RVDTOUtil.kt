package com.jpromi.spaceview.dtos.roomvox

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

fun String.toRoomVoxLocalDateTimeOrNull(): LocalDateTime? {
    val value = trim().replace(" ", "T")
    val localValue = value
        .substringBefore("Z")
        .substringBefore("+")
        .let { withoutPositiveOffset ->
            val offsetStart = withoutPositiveOffset.indexOf('-', startIndex = 10)
            if (offsetStart >= 0) withoutPositiveOffset.take(offsetStart) else withoutPositiveOffset
        }

    return runCatching {
        when {
            localValue.length >= 19 && localValue[4] == '-' -> {
                LocalDateTime.parse(localValue.take(19))
            }
            localValue.length >= 15 && localValue[8] == 'T' -> {
                val date = LocalDate(
                    year = localValue.substring(0, 4).toInt(),
                    monthNumber = localValue.substring(4, 6).toInt(),
                    dayOfMonth = localValue.substring(6, 8).toInt(),
                )
                val time = LocalTime(
                    hour = localValue.substring(9, 11).toInt(),
                    minute = localValue.substring(11, 13).toInt(),
                    second = localValue.substring(13, 15).toInt(),
                )
                LocalDateTime(date, time)
            }
            localValue.length == 10 && localValue[4] == '-' -> {
                LocalDateTime(LocalDate.parse(localValue), LocalTime(0, 0))
            }
            localValue.length == 8 -> {
                LocalDateTime(
                    LocalDate(
                        year = localValue.substring(0, 4).toInt(),
                        monthNumber = localValue.substring(4, 6).toInt(),
                        dayOfMonth = localValue.substring(6, 8).toInt(),
                    ),
                    LocalTime(0, 0),
                )
            }
            else -> LocalDateTime.parse(localValue)
        }
    }.getOrNull()
}