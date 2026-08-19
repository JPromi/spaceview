package com.jpromi.spaceview.controllers

import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState

actual class FullscreenController {
    actual companion object {
        actual fun setFullscreen(enabled: Boolean) {
            AppWindowState.windowState.placement =
                if (enabled) WindowPlacement.Fullscreen else WindowPlacement.Floating
        }
        actual fun isFullscreenSupported() = true
    }

}

object AppWindowState {
    lateinit var windowState: WindowState
}