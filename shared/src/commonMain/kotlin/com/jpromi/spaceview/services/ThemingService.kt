package com.jpromi.spaceview.services

import androidx.compose.ui.graphics.Color
import com.jpromi.spaceview.models.Image
import com.jpromi.spaceview.models.Room

interface ThemingService {
    // get BackgroundColor
    // get Images

    var baseUrl: String

    suspend fun getThemeColor(): Color?

    suspend fun getLogo(): String?

    suspend fun getBackgroundImage(): String?

    suspend fun getImageLibrary(): List<Image>
}
