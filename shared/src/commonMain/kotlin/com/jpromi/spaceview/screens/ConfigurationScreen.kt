package com.jpromi.spaceview.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jpromi.spaceview.AppColor
import com.jpromi.spaceview.AppSettings
import com.jpromi.spaceview.CalendarSettings
import com.jpromi.spaceview.elements.forms.SettingsButton
import com.jpromi.spaceview.elements.forms.SettingsDropdown
import com.jpromi.spaceview.elements.forms.SettingsTextInput
import com.jpromi.spaceview.enums.CalendarProviderENUM
import com.jpromi.spaceview.models.CalendarProvider
import com.jpromi.spaceview.models.Room
import com.jpromi.spaceview.network.ApiResult
import com.jpromi.spaceview.services.RoomService
import com.jpromi.spaceview.services.impl.RoomVoxRoomService
import kotlinx.coroutines.launch

@Composable
fun ConfigurationScreen(
    onGoBack: () -> Unit,
    appSettings: AppSettings = remember { AppSettings() },
    calendarSettings: CalendarSettings = remember { CalendarSettings() },
    roomService: RoomService = remember { RoomVoxRoomService(calendarSettings) },
) {
    var selectedProvider by remember {
        mutableStateOf(calendarSettings.calendarProvider ?: CalendarProviderENUM.DEMO)
    }

    var selectedRoomVoxServerUrl by remember {
        mutableStateOf(calendarSettings.roomVoxServerUrl)
    }
    var selectedRoomVoxToken by remember {
        mutableStateOf(calendarSettings.roomVoxAccessToken)
    }
    var isCheckingRoomVoxConnection by remember { mutableStateOf(false) }

    // tmp
    val coroutineScope = rememberCoroutineScope()
    var remoteServerConnection by remember { mutableStateOf(false) }
    var remoteServerConnectionMessage by remember { mutableStateOf<String?>(null) }

    var loadedRooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var selectedRoomId by remember { mutableStateOf(calendarSettings.selectedRoomId) }

    var isLoadingRooms by remember { mutableStateOf(false) }
    var loadRoomsMessage by remember { mutableStateOf<String?>(null) }

    fun loadRooms() {
        roomService.configure(
            serverUrl = selectedRoomVoxServerUrl,
            accessToken = selectedRoomVoxToken,
        )

        coroutineScope.launch {
            isLoadingRooms = true
            loadRoomsMessage = null

            when (val result = roomService.getRooms()) {
                is ApiResult.Success -> {
                    loadedRooms = result.data
                    if (loadedRooms.none { it.id == selectedRoomId }) {
                        selectedRoomId = ""
                    }
                    loadRoomsMessage = "${loadedRooms.size} Räume geladen"
                }
                else -> {
                    loadedRooms = emptyList()
                    loadRoomsMessage = "Räume konnten nicht geladen werden"
                }
            }

            isLoadingRooms = false
        }
    }

    fun checkConnection() {
        roomService.configure(
            serverUrl = selectedRoomVoxServerUrl,
            accessToken = selectedRoomVoxToken,
        )

        coroutineScope.launch {
            roomService.configure(
                serverUrl = selectedRoomVoxServerUrl,
                accessToken = selectedRoomVoxToken,
            )

            isCheckingRoomVoxConnection = true
            remoteServerConnectionMessage = null
            remoteServerConnection = false

            loadedRooms = emptyList()
            loadRoomsMessage = null

            remoteServerConnectionMessage = when (
                roomService.checkCredentials()
            ) {
                is ApiResult.Success -> {
                    remoteServerConnection = true
                    loadRooms()
                    "Verbunden"
                }
                is ApiResult.Unauthorized -> "Token falsch"
                is ApiResult.NetworkError -> "Network error"
                is ApiResult.NotFound -> "Not found"
                is ApiResult.Forbidden -> "Forbidden"
                else -> "Unknown error"
            }

            isCheckingRoomVoxConnection = false
        }
    }

    fun save() {
        // Calendar Settings
        calendarSettings.calendarProvider = selectedProvider

        // RoomVox
        if (selectedProvider == CalendarProviderENUM.ROOMVOX) {
            calendarSettings.roomVoxServerUrl = selectedRoomVoxServerUrl
            calendarSettings.roomVoxAccessToken = selectedRoomVoxToken
        } else {
            calendarSettings.roomVoxServerUrl = ""
            calendarSettings.roomVoxAccessToken = ""
        }

        // set Room ID
        if (selectedProvider == CalendarProviderENUM.ROOMVOX) {
            calendarSettings.selectedRoomId = selectedRoomId
        } else {
            calendarSettings.selectedRoomId = ""
        }

        // leave
        onGoBack()
    }

    // on open
    LaunchedEffect(Unit) {
        checkConnection()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(AppColor.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColor.backgroundSettings, shape = RoundedCornerShape(12.dp))
                .border(.5.dp, AppColor.borderSettings, shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Provider",
                color = AppColor.textColor
            )

            val providers: List<CalendarProvider> = listOf(
                CalendarProvider(
                    id = CalendarProviderENUM.DEMO,
                    name = "Demo"
                ),
                CalendarProvider(
                    id = CalendarProviderENUM.ROOMVOX,
                    name = "RoomVox"
                ),
            )

            SettingsDropdown(
                label = "Provider auswaehlen",
                options = providers,
                selectedOption = providers.find { it.id == selectedProvider },
                optionText = { it.name },
                onOptionSelected = { provider ->
                    selectedProvider = provider.id
                }
            )

            when (selectedProvider) {
                CalendarProviderENUM.ROOMVOX -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(.5.dp, AppColor.borderSettings, shape = RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "RoomVox (Nextcloud) Provider",
                            color = AppColor.textColor
                        )

                        SettingsTextInput(
                            label = "Server",
                            value = selectedRoomVoxServerUrl,
                            onValueChange = {
                                selectedRoomVoxServerUrl = it
                                remoteServerConnectionMessage = null
                            },
                            keyboardType = KeyboardType.Uri
                        )

                        SettingsTextInput(
                            label = "Token",
                            value = selectedRoomVoxToken,
                            onValueChange = {
                                selectedRoomVoxToken = it
                                remoteServerConnectionMessage = null
                            },
                            keyboardType = KeyboardType.Text
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            if (remoteServerConnectionMessage != null) {
                                Text(
                                    text = remoteServerConnectionMessage!!
                                )
                            } else {
                                Text("")
                            }

                            SettingsButton(
                                text = if (isCheckingRoomVoxConnection) {
                                    "Pruefe..."
                                } else {
                                    "Verbindung pruefen"
                                },
                                enabled = !isCheckingRoomVoxConnection,
                                onClick = { checkConnection() },
                                modifier = Modifier.width(200.dp),
                            )
                        }
                    }
                }

                else -> null
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColor.backgroundSettings, shape = RoundedCornerShape(12.dp))
                .border(.5.dp, AppColor.borderSettings, shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Kalender",
                color = AppColor.textColor
            )

            SettingsDropdown(
                label = "Raum auswählen",
                options = loadedRooms,
                selectedOption = loadedRooms.find { it.id == selectedRoomId },
                optionText = { room -> room.name },
                onOptionSelected = { room ->
                    selectedRoomId = room.id
                }
            )

            // allow edit
        }

        // Buttons
        Row() {
            SettingsButton(
                text = "Speichern",
                enabled = remoteServerConnection,
                onClick = { save() },
                modifier = Modifier.width(200.dp),
            )
        }
    }
}
