package com.jpromi.spaceview.controllers

import androidx.compose.runtime.staticCompositionLocalOf

val LocalFullscreenController = staticCompositionLocalOf<FullscreenController?> { null }

interface FullscreenController {
    fun setFullscreen(enabled: Boolean)
}