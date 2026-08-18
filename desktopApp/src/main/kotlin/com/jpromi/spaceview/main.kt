package com.jpromi.spaceview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import java.awt.Dimension

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SpaceView",
        resizable = true,
        state = WindowState(width = 1000.dp, height = 600.dp)
    ) {
        window.minimumSize = Dimension(800, 600)
        App()
    }
}