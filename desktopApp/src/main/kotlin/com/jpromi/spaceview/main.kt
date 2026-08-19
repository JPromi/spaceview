package com.jpromi.spaceview

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.jpromi.spaceview.controllers.AppWindowState
import com.jpromi.spaceview.controllers.FullscreenController
import java.awt.Dimension

fun main() = application {
    val windowState = rememberWindowState(size = DpSize(1000.dp, 600.dp))
    AppWindowState.windowState = windowState

    val appSettings = AppSettings()
    FullscreenController.setFullscreen(appSettings.fullscreen)


    Window(
        onCloseRequest = ::exitApplication,
        title = "SpaceView",
        resizable = true,
        state = windowState
    ) {
        window.minimumSize = Dimension(800, 600)
        App()
    }
}