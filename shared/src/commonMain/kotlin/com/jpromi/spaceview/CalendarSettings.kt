package com.jpromi.spaceview

import com.jpromi.spaceview.enums.CalendarProviderENUM
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

class CalendarSettings(
    private val settings: Settings = Settings()
) {
    var calendarProvider: CalendarProviderENUM?
        get() = settings.getStringOrNull(KEY_PROVIDER)
            ?.let { runCatching { CalendarProviderENUM.valueOf(it) }.getOrNull() }
        set(value) {
            if (value == null) {
                settings.remove(KEY_PROVIDER)
            } else {
                settings.putString(KEY_PROVIDER, value.name)
            }
        }

    // RoomVox Nextcloud
    var roomVoxServerUrl: String
        get() = settings.getString(KEY_ROOMVOX_SERVER_URL, "")
        set(value) {
            settings.putString(KEY_ROOMVOX_SERVER_URL, value)
        }

    var roomVoxAccessToken: String
        get() = settings.getString(KEY_ROOMVOX_ACCESS_TOKEN, "")
        set(value) {
            settings.putString(KEY_ROOMVOX_ACCESS_TOKEN, value)
        }

    var roomVoxAccessTokenPermission: String
        // read, book, admin
        get() = settings.getString(KEY_ROOMVOX_ACCESS_TOKEN_PERMISSION, "")
        set(value) {
            settings.putString(KEY_ROOMVOX_ACCESS_TOKEN_PERMISSION, value)
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
        const val KEY_PROVIDER = "calendar_provider"
        const val KEY_ROOMVOX_SERVER_URL = "calendar_roomvox_server_url"
        const val KEY_ROOMVOX_ACCESS_TOKEN = "calendar_roomvox_access_token"
        const val KEY_ROOMVOX_ACCESS_TOKEN_PERMISSION = "calendar_roomvox_access_token_permission"
        const val KEY_SELECTED_ROOM_ID = "calendar_selected_room_id"
        const val KEY_SHOW_ADD_EVENT = "calendar_selected_show_add_event"
    }
}