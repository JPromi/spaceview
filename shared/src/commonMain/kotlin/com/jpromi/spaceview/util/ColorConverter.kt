package com.jpromi.spaceview.util

import androidx.compose.ui.graphics.Color

fun Color.toHexCode(): String {
    val red = this.red * 255
    val green = this.green * 255
    val blue = this.blue * 255
    val alpha = this.alpha * 255

    if (alpha == 255f) {
        return String.format("#%02x%02x%02x", red.toInt(), green.toInt(), blue.toInt())
    } else {
        return String.format("#%02x%02x%02x%02x", red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
    }

}

fun String.toColor(): Color {
    val hex = removePrefix("#")

    return when (hex.length) {
        6 -> Color(("FF$hex").toULong(16))
        8 -> Color(hex.toULong(16))
        else -> throw IllegalArgumentException("Invalid color: $this")
    }
}