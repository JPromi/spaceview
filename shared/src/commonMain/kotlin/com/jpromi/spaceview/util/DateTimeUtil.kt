package com.jpromi.spaceview.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format

fun LocalDateTime.toMinuteOfDay(): Int = hour * 60 + minute

fun LocalDateTime.toTimeText(): String = this.format(LocalDateTime.Format {
    hour()
    chars(":")
    minute()
})

fun LocalDateTime.toDateText(): String = this.format(LocalDateTime.Format {
    day()
    chars(".")
    monthNumber()
    chars(".")
    year()
})