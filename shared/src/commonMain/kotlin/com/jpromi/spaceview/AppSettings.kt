package com.jpromi.spaceview

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

class AppSettings(
    private val settings: Settings = Settings()
) {
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


    private companion object {
        const val KEY_ADMIN_PIN = "admin_pin"
        const val KEY_FULLSCREEN = "selected_fullscreen"
        const val KEY_THEME_COLOR = "theme_color"
    }
}
