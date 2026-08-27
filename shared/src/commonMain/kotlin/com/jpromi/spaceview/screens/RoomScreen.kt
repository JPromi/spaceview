package com.jpromi.spaceview.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.Settings
import com.jpromi.spaceview.AppTheme
import com.jpromi.spaceview.AppSettings
import com.jpromi.spaceview.CalendarSettings
import com.jpromi.spaceview.dtos.roomvox.RVRoomAvailabilityDTO
import com.jpromi.spaceview.dtos.roomvox.RVRoomStatusDTO
import com.jpromi.spaceview.elements.AdminPinPopup
import com.jpromi.spaceview.elements.AppInfoPopup
import com.jpromi.spaceview.elements.roomscreen.DateTimeView
import com.jpromi.spaceview.elements.roomscreen.NameStatusView
import com.jpromi.spaceview.elements.roomscreen.RoundIconButton
import com.jpromi.spaceview.elements.roomscreen.SlotView
import com.jpromi.spaceview.enums.CalendarProviderENUM
import com.jpromi.spaceview.enums.SlotStatus
import com.jpromi.spaceview.models.Room
import com.jpromi.spaceview.models.RoomUse
import com.jpromi.spaceview.models.Slot
import com.jpromi.spaceview.network.ApiResult
import com.jpromi.spaceview.network.toUserMessage
import com.jpromi.spaceview.services.RoomService
import com.jpromi.spaceview.services.impl.DemoRoomService
import com.jpromi.spaceview.services.impl.RoomVoxRoomService
import com.jpromi.spaceview.util.toMinuteOfDay
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import com.jpromi.spaceview.util.toTimeText
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun RoomScreen(
    onOpenConfiguration: () -> Unit,
    appSettings: AppSettings = remember { AppSettings() },
    calendarSettings: CalendarSettings = CalendarSettings(),
) {
    var roomService by remember { mutableStateOf<RoomService>(DemoRoomService()) }

    var roomUse by remember { mutableStateOf<RoomUse?>(null) }
    var room by remember { mutableStateOf<Room?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoadingRoom by remember { mutableStateOf(true) }
    var isLoadingAvailability by remember { mutableStateOf(true) }
    var currentMinuteOfDay by remember { mutableStateOf(0) }
    var currentTime by remember {
        mutableStateOf<LocalDateTime>(
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }
    val coroutineScope = rememberCoroutineScope()

    var isAdminPinPopupVisible by remember { mutableStateOf(false) }
    var isAppInfoPopupVisible by remember { mutableStateOf(false) }


    fun initRoomService() {
        when (calendarSettings.calendarProvider) {
            CalendarProviderENUM.ROOMVOX -> {
                roomService = RoomVoxRoomService()
            }

            else -> {
                roomService = DemoRoomService()
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            currentMinuteOfDay = currentTime.toMinuteOfDay()
            delay(30.seconds)
        }
    }

    suspend fun loadRoom() {
        when (val result = roomService.getRoomById(calendarSettings.selectedRoomId)) {
            is ApiResult.Success -> room = result.data
            is ApiResult.Error -> {
                room = null
                errorMessage = result.toUserMessage()
            }
        }
        isLoadingRoom = false
    }

    suspend fun loadRoomUse() {
        when (val result = roomService.getRoomUse(calendarSettings.selectedRoomId)) {
            is ApiResult.Success -> roomUse = result.data
            is ApiResult.Error -> {
                roomUse = null
                errorMessage = result.toUserMessage()
            }
        }
        isLoadingAvailability = false
    }

    LaunchedEffect(calendarSettings.selectedRoomId, calendarSettings.calendarProvider) {
        initRoomService()
        isLoadingRoom = true
        isLoadingAvailability = true

        while (true) {
            loadRoom()
            loadRoomUse()

            delay(30.seconds) // 30 seconds
        }
    }

    // UI
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.background)
            .padding(32.dp)
    ) {
        if (isLoadingRoom || isLoadingAvailability) {
            // loading
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier.weight(4f).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Datetime
                Column(modifier = Modifier.weight(1f)) {
                    DateTimeView(currentTime)
                }

                // Name & Status
                Column(
                    modifier = Modifier.weight(2f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    NameStatusView(room, roomUse, currentMinuteOfDay)
                }

                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    //Logo
                    Box()
                    {
                        if (calendarSettings.showLogo) {

                            AsyncImage(
                                model = roomService.getLogoUrl(), // works whether it resolves to .png or .svg
                                contentDescription = "Logo",
                                modifier = Modifier.fillMaxHeight()
                            )
                        }
                    }

                    // Settings
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    )
                    {
                        RoundIconButton(
                            onClick = { isAppInfoPopupVisible = true },
                            icon = Lucide.Info
                        )
                        
                        RoundIconButton(
                            onClick = if (appSettings.adminPin.isNotEmpty()) {
                                { isAdminPinPopupVisible = true }
                            } else {
                                onOpenConfiguration
                            }, icon = Lucide.Settings)

                        RoundIconButton(
                            onClick =
                                {
                                    isLoadingRoom = true
                                    isLoadingAvailability = true
                                    coroutineScope.launch {
                                        loadRoom()
                                        loadRoomUse()
                                        isLoadingRoom = false
                                        isLoadingAvailability = false
                                    }
                                },
                            icon =Lucide.RefreshCw
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(3f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                // Slots
                Column(modifier = Modifier.fillMaxHeight().weight(1f)) {
                    SlotView(roomUse, currentMinuteOfDay)
                }

                if (calendarSettings.showAddEvent) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.DarkGray,
                        ),
                        onClick = onOpenConfiguration
                    ) {
                        Text("Neuer Termin")
                    }
                }

            }
        }
    }

    if (isAdminPinPopupVisible) {
        AdminPinPopup(
            onValidPinEnteredFunction = {
                onOpenConfiguration()
            },
            onDismiss = { isAdminPinPopupVisible = false },
            appSettings = appSettings
        )
    }

    // App Info Popup
    if (isAppInfoPopupVisible) {
        AppInfoPopup(
            onDismiss = { isAppInfoPopupVisible = false }
        )
    }
}