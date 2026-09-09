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

    val argb = when (hex.length) {
        3 -> "FF${hex[0]}${hex[0]}${hex[1]}${hex[1]}${hex[2]}${hex[2]}"
        6 -> "FF$hex"
        8 -> hex
        else -> throw IllegalArgumentException("Invalid color: $this")
    }

    return Color(argb.toLong(16).toInt())
}
