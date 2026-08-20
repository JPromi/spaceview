package com.jpromi.spaceview

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.jpromi.spaceview.controllers.FullscreenController
import com.jpromi.spaceview.controllers.LocalFullscreenController
import java.awt.Dimension

fun main() = application {
    val windowState = rememberWindowState(size = DpSize(1000.dp, 600.dp))

    val holder = remember {
        object : FullscreenController {
            override fun setFullscreen(enabled: Boolean) {
                windowState.placement =
                    if (enabled) WindowPlacement.Fullscreen else WindowPlacement.Floating
            }
        }
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "SpaceView",
        resizable = true,
        state = windowState
    ) {
        window.minimumSize = Dimension(800, 600)
        CompositionLocalProvider(LocalFullscreenController provides holder) {
            App()
        }
    }
}