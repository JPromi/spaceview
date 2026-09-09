package com.jpromi.spaceview.util

import androidx.compose.ui.graphics.Color

fun Color.toHexCode(): String {
    val red = (this.red * 255).toInt().toHexByte()
    val green = (this.green * 255).toInt().toHexByte()
    val blue = (this.blue * 255).toInt().toHexByte()
    val alpha = (this.alpha * 255).toInt().toHexByte()

    if (alpha == "ff") {
        return "#$red$green$blue"
    } else {
        return "#$red$green$blue$alpha"
    }

}

private fun Int.toHexByte(): String =
    coerceIn(0, 255).toString(16).padStart(2, '0')

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
