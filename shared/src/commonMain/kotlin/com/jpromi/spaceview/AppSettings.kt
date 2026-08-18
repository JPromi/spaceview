package com.jpromi.spaceview

import com.russhwolf.settings.Settings

class AppSettings(
    private val settings: Settings = Settings()
) {
    var serverUrl: String
        get() = settings.getString(KEY_SERVER_URL, "")
        set(value) {
            settings.putString(KEY_SERVER_URL, value)
        }

    var accessToken: String
        get() = settings.getString(KEY_ACCESS_TOKEN, "")
        set(value) {
            settings.putString(KEY_ACCESS_TOKEN, value)
        }

    var accessTokenPermission: String
        // read, book, admin
        get() = settings.getString(KEY_ACCESS_TOKEN_PERMISSION, "")
        set(value) {
            settings.putString(KEY_ACCESS_TOKEN_PERMISSION, value)
        }

    var adminPin: String
        get() = settings.getString(KEY_ADMIN_PIN, "")
        set(value) {
            settings.putString(KEY_ADMIN_PIN, value)
        }

    var selectedRoomId: String
        get() = settings.getString(KEY_SELECTED_ROOM_ID, "")
        set(value) {
            settings.putString(KEY_SELECTED_ROOM_ID, value)
        }

    var showAddEvent: Boolean
        get() = settings.getBoolean(KEY_SHOW_ADD_EVENT, false)
        set(value) {
            settings.putBoolean(KEY_SHOW_ADD_EVENT, value)
        }

    private companion object {
        const val KEY_SERVER_URL = "server_url"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_ACCESS_TOKEN_PERMISSION = "access_token_permission"
        const val KEY_SELECTED_ROOM_ID = "selected_room_id"
        const val KEY_SHOW_ADD_EVENT = "selected_show_add_event"
        const val KEY_ADMIN_PIN = "admin_pin"
    }
}