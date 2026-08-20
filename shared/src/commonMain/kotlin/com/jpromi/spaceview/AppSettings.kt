package com.jpromi.spaceview

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

    private companion object {
        const val KEY_ADMIN_PIN = "admin_pin"
        const val KEY_FULLSCREEN = "selected_fullscreen"
    }
}