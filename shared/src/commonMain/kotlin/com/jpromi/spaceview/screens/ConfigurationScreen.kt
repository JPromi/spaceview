package com.jpromi.spaceview.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jpromi.spaceview.AppSettings
import com.jpromi.spaceview.CalendarSettings
import com.jpromi.spaceview.dtos.roomvox.RVRoomDTO
import com.jpromi.spaceview.network.ApiResult
import com.jpromi.spaceview.network.RoomVoxService
import com.jpromi.spaceview.network.ServerConnectionResult
import com.jpromi.spaceview.network.toUserMessage
import kotlinx.coroutines.launch

@Composable
fun ConfigurationScreen(
    onGoBack: () -> Unit,
    appSettings: AppSettings = remember { AppSettings() },
    calendarSettings: CalendarSettings = remember { CalendarSettings() },
    roomVoxService: RoomVoxService = remember { RoomVoxService(appSettings) },
) {
    var serverUrl by remember { mutableStateOf(calendarSettings.roomVoxServerUrl) }
    var accessToken by remember { mutableStateOf(calendarSettings.roomVoxAccessToken) }
    var serverCheckResult by remember { mutableStateOf<ServerConnectionResult?>(null) }
    var isCheckingServer by remember { mutableStateOf(false) }
    var isRoomMenuExpanded by remember { mutableStateOf(false) }
    var selectedRoomId by remember { mutableStateOf(calendarSettings.selectedRoomId) }
    val coroutineScope = rememberCoroutineScope()

    val rooms = remember { mutableStateListOf<RVRoomDTO>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Konfiguration")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = serverUrl,
            onValueChange = {
                serverUrl = it
                calendarSettings.roomVoxServerUrl = it
                serverCheckResult = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Server URL") },
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = accessToken,
            onValueChange = {
                accessToken = it
                calendarSettings.roomVoxAccessToken = it
                serverCheckResult = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Access Token") },
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    isCheckingServer = true
                    serverCheckResult = null
                    serverCheckResult = when (val result = roomVoxService.getRooms()) {
                        is ApiResult.Success -> {
                            rooms.clear()
                            rooms.addAll(result.data)
                            if (rooms.none { it.id == selectedRoomId }) {
                                selectedRoomId = ""
                                calendarSettings.selectedRoomId = ""
                            }
                            ServerConnectionResult.Success(
                                "Verbindung OK. ${rooms.size} Räume gefunden."
                            )
                        }
                        is ApiResult.Error -> {
                            ServerConnectionResult.Error(result.toUserMessage())
                        }
                    }
                    isCheckingServer = false
                }
            },
            enabled = !isCheckingServer,
        ) {
            Text(if (isCheckingServer) "Pruefe..." else "Server pruefen")
        }

        serverCheckResult?.let { result ->
            Spacer(modifier = Modifier.height(8.dp))

            val message = when (result) {
                is ServerConnectionResult.Success -> result.message
                is ServerConnectionResult.Error -> result.message
            }

            Text(message)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (rooms.isNotEmpty()) {
            val selectedRoom = rooms.firstOrNull { it.id == selectedRoomId }

            Text("Verfügbarer Raum:")

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { isRoomMenuExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(selectedRoom?.name ?: "Raum auswählen")
                }

                DropdownMenu(
                    expanded = isRoomMenuExpanded,
                    onDismissRequest = { isRoomMenuExpanded = false },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    rooms.forEach { room ->
                        DropdownMenuItem(
                            text = { Text("${room.name} (ID: ${room.id})") },
                            onClick = {
                                selectedRoomId = room.id
                                calendarSettings.selectedRoomId = room.id
                                isRoomMenuExpanded = false
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // settings
        var showAddEvent by remember { mutableStateOf(calendarSettings.showAddEvent) }

        Switch(
            checked = showAddEvent,
            onCheckedChange = {
                showAddEvent = it
                calendarSettings.showAddEvent = it
            },
        )
        Text("Show Add Event")

        // Admin Pin configuration
        var adminPin by remember { mutableStateOf(appSettings.adminPin) }
        val isAdminPinValid = adminPin.isEmpty() || adminPin.length == 4

        OutlinedTextField(
            value = adminPin,
            onValueChange = { value ->
                val sanitizedValue = value
                    .filter { it.isDigit() }
                    .take(4)

                adminPin = sanitizedValue

                if (sanitizedValue.isEmpty() || sanitizedValue.length == 4) {
                    appSettings.adminPin = sanitizedValue
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Admin PIN") },
            supportingText = {
                if (!isAdminPinValid) {
                    Text("PIN muss leer oder 4-stellig sein")
                }
            },
            isError = !isAdminPinValid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            singleLine = true,
        )

        Button(onClick = onGoBack) {
            Text("Zurueck")
        }
    }
}
