package com.jpromi.spaceview

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.jpromi.spaceview.enums.AssetSourceType
import com.jpromi.spaceview.models.Image
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import kotlinx.serialization.json.Json

class AppSettings(
    private val settings: Settings = Settings()
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    var adminPin: String
        get() = settings.getString(KEY_ADMIN_PIN, "")
        set(value) {
            settings.putString(KEY_ADMIN_PIN, value)
        }

    var fullscreen: Boolean
        get() = settings.getBoolean(KEY_FULLSCREEN, false)
        set(value) {
            settings.putBoolean(KEY_FULLSCREEN, value)
        }

    var themeColor: Color?
        get() = if (settings.hasKey(KEY_THEME_COLOR)) {
            Color(settings.getInt(KEY_THEME_COLOR, 0))
        } else {
            null
        }
        set(value) {
            if (value != null) {
                settings.putInt(KEY_THEME_COLOR, value.toArgb())
            } else {
                settings.remove(KEY_THEME_COLOR)
            }
        }

    var logo: Image?
        get() = settings.getStringOrNull(KEY_THEME_LOGO)?.let {
            runCatching {
                json.decodeFromString<Image>(it)
            }.getOrNull()
        }
        set(value) {
            if (value != null) {
                settings.putString(
                    KEY_THEME_LOGO,
                    json.encodeToString(value)
                )
            } else {
                settings.remove(KEY_THEME_LOGO)
            }
        }

    var backgroundImage: Image?
        get() = settings.getStringOrNull(KEY_THEME_BACKGROUND_IMAGE)?.let {
            runCatching {
                json.decodeFromString<Image>(it)
            }.getOrNull()
        }
        set(value) {
            if (value != null) {
                settings.putString(
                    KEY_THEME_BACKGROUND_IMAGE,
                    json.encodeToString(value)
                )
            } else {
                settings.remove(KEY_THEME_BACKGROUND_IMAGE)
            }
        }


    private companion object {
        const val KEY_ADMIN_PIN = "admin_pin"
        const val KEY_FULLSCREEN = "selected_fullscreen"
        const val KEY_THEME_COLOR = "theme_color"
        const val KEY_THEME_LOGO = "theme_logo"
        const val KEY_THEME_BACKGROUND_IMAGE = "theme_background_image"
    }
}
