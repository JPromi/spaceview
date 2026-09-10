package com.jpromi.spaceview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jpromi.spaceview.controllers.LocalFullscreenController
import com.jpromi.spaceview.elements.ImageView
import com.jpromi.spaceview.screens.ConfigurationScreen
import com.jpromi.spaceview.screens.RoomScreen

private enum class Screen {
    Configuration,
    Room
}

@Composable
@Preview
fun App() {
    val appSettings = remember { AppSettings() }
    var themeColor by remember { mutableStateOf(appSettings.themeColor) }
    LocalFullscreenController.current?.setFullscreen(appSettings.fullscreen);

    AppTheme(themeColor = themeColor) {
        var currentScreen by remember { mutableStateOf(Screen.Room) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.background),
        ) {
            // Background
            if (appSettings.backgroundImage != null) {
                ImageView(
                    image = appSettings.backgroundImage,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            // App
            when (currentScreen) {
                Screen.Configuration -> ConfigurationScreen(
                    onGoBack = {
                        themeColor = appSettings.themeColor
                        currentScreen = Screen.Room
                    },
                    appSettings = appSettings,
                )
                Screen.Room -> RoomScreen(
                    onOpenConfiguration = { currentScreen = Screen.Configuration },
                    appSettings = appSettings,
                )
            }
        }

    }
}
