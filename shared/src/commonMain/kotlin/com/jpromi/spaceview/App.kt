package com.jpromi.spaceview

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.jpromi.spaceview.controllers.LocalFullscreenController
import com.jpromi.spaceview.screens.ConfigurationScreen
import com.jpromi.spaceview.screens.RoomScreen

private enum class Screen {
    Configuration,
    Room
}

@Composable
@Preview
fun App() {
    val appSettings = AppSettings();
    LocalFullscreenController.current?.setFullscreen(appSettings.fullscreen);

    AppTheme {
        var currentScreen by remember { mutableStateOf(Screen.Room) }

        when (currentScreen) {
            Screen.Configuration -> ConfigurationScreen(
                onGoBack = { currentScreen = Screen.Room }
            )
            Screen.Room -> RoomScreen(
                onOpenConfiguration = { currentScreen = Screen.Configuration },
            )
        }
    }
}
