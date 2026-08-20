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

    private companion object {
        const val KEY_ADMIN_PIN = "admin_pin"
    }
}