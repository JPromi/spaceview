package com.jpromi.spaceview.services

import androidx.compose.ui.graphics.Color
import com.jpromi.spaceview.models.Room

interface ThemingService {
    // get Logo
    // get BackgroundImage
    // get BackgroundColor
    // get Images
    // get ThemeColor

    var baseUrl: String

    suspend fun getThemeColor(): Color?

    suspend fun getLogo(): String?

    suspend fun getBackgroundImage(): String?
}
