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
import com.composables.icons.lucide.Settings
import com.jpromi.spaceview.AppTheme
import com.jpromi.spaceview.AppSettings
import com.jpromi.spaceview.CalendarSettings
import com.jpromi.spaceview.dtos.roomvox.RVRoomAvailabilityDTO
import com.jpromi.spaceview.dtos.roomvox.RVRoomStatusDTO
import com.jpromi.spaceview.elements.AdminPinPopup
import com.jpromi.spaceview.elements.AppInfoPopup
import com.jpromi.spaceview.elements.roomscreen.DateTimeView
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
    var currentTime by remember { mutableStateOf<LocalDateTime>(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) }

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

    LaunchedEffect(calendarSettings.selectedRoomId, calendarSettings.calendarProvider) {
        initRoomService()
        isLoadingRoom = true
        isLoadingAvailability = true

        while (true) {
            when (val result = roomService.getRoomById(calendarSettings.selectedRoomId)) {
                is ApiResult.Success -> room = result.data
                is ApiResult.Error -> {
                    room = null
                    errorMessage = result.toUserMessage()
                }
            }
            isLoadingRoom = false

            when (val result = roomService.getRoomUse(calendarSettings.selectedRoomId)) {
                is ApiResult.Success -> roomUse = result.data
                is ApiResult.Error -> {
                    roomUse = null
                    errorMessage = result.toUserMessage()
                }
            }
            isLoadingAvailability = false
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
                    Text(
                        text = room?.name ?: "",
                        modifier = Modifier.padding(bottom = 4.dp),
                        fontWeight = FontWeight.W500,
                        fontSize = 30.sp,
                        color = AppTheme.textColor,
                    )

                    Spacer(modifier = Modifier.height(24.dp))


                    // status
                    Box(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = if (roomUse?.currentEvent != null) {
                                    AppTheme.busyTagBackground
                                } else {
                                    AppTheme.freeTagBackground
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        if (roomUse?.currentEvent != null) {
                                            AppTheme.busyTagBackground
                                        } else {
                                            AppTheme.freeTagBackground
                                        }.copy(alpha = 0.25f),
                                        Color.Transparent
                                    ),
                                ),
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(12.dp)
                            .height(90.dp)
                            .fillMaxWidth()
                    ) {
                        if (roomUse?.currentEvent != null) {
                            // busy
                            Column(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Belegt",
                                    color = AppTheme.textColor,
                                    fontWeight = FontWeight.W700,
                                    fontSize = 32.sp,
                                    lineHeight = 10.sp,
                                )
                                // ToDo: Show current termin, remaining minutes,...
                            }

                        } else {
                            // free
                            Column(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Frei",
                                    color = AppTheme.textColor,
                                    fontWeight = FontWeight.W700,
                                    fontSize = 32.sp,
                                    lineHeight = 10.sp,
                                )

                                var freeUntil = roomUse?.slots.orEmpty().filter { slot ->
                                    slot.start.toMinuteOfDay() > currentMinuteOfDay && slot.status == SlotStatus.BOOKED
                                }.minByOrNull { slot -> slot.start.toMinuteOfDay() }?.end?.toTimeText()

                                if (freeUntil == null)
                                    freeUntil = roomUse?.slots.orEmpty()
                                        .maxBy { slot -> slot.end.toMinuteOfDay() }.end.toTimeText()

                                Text(
                                    text = "bis ${freeUntil}",
                                    color = AppTheme.textColor,
                                    fontSize = 18.sp,
                                    lineHeight = 18.sp,
                                )
                            }
                        }

                    }
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
                        IconButton(
                            onClick = { isAppInfoPopupVisible = true },
                            modifier = Modifier
                                .size(48.dp)
                                .border(
                                    width = 1.dp,
                                    color = AppTheme.textColor,
                                    shape = CircleShape
                                ),
                        ) {
                            Icon(
                                imageVector = Lucide.Info,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp),
                            )
                        }

                        IconButton(
                            onClick = if (appSettings.adminPin.isNotEmpty()) {
                                { isAdminPinPopupVisible = true }
                            } else {
                                onOpenConfiguration
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .border(
                                    width = 1.dp,
                                    color = AppTheme.textColor,
                                    shape = CircleShape
                                ),
                        ) {
                            Icon(
                                imageVector = Lucide.Settings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(3f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(6.dp)) {

                // Slots
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isLoadingAvailability) {
                        CircularProgressIndicator()
                    } else {
                        val slots = roomUse?.slots.orEmpty().filter { slot ->
                            slot.end.toMinuteOfDay() > currentMinuteOfDay
                        }

                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        ) {
                            val spacing = 6.dp
                            val totalSpacing = spacing * (slots.size - 1).coerceAtLeast(0)
                            val availableSlotHeight = maxOf(0.dp, maxHeight - totalSpacing)
                            val durations = slots.map {
                                (it.end.toMinuteOfDay() - it.start.toMinuteOfDay())
                                    .coerceAtLeast(1)
                                    .toLong()
                            }
                            val slotHeights = calculateSlotHeights(
                                availableHeight = availableSlotHeight,
                                durations = durations,
                                minHeight = 70.dp,
                            )

                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(spacing),
                            ) {
                                slots.forEachIndexed { index, slot ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(slotHeights[index])
                                            .background(
                                                color = AppTheme.slotBackground,
                                                shape = RoundedCornerShape(12.dp),
                                            )
                                            .padding(8.dp),
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Status
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        color = if (slot.status == SlotStatus.BOOKED) {
                                                            AppTheme.busyTagBackground
                                                        } else {
                                                            AppTheme.freeTagBackground
                                                        },
                                                        shape = RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(vertical = 2.dp, horizontal = 6.dp)
                                            ) {
                                                Text(
                                                    text = slot.status.toString(),
                                                    color = if (slot.status == SlotStatus.BOOKED) {
                                                        AppTheme.busyTabTextColor
                                                    } else {
                                                        AppTheme.freeTabTextColor
                                                    },
                                                )
                                            }

                                            // Time
                                            Text(
                                                text = "${slot.start.toTimeText()} - ${slot.end.toTimeText()}",
                                                color = AppTheme.textColor,
                                            )
                                        }


                                        // Title
                                        Text(
                                            text = if (slot.status == SlotStatus.BOOKED) {
                                                slot.event?.title ?: "Belegt"
                                            } else {
                                                "Frei"
                                            },
                                            color = AppTheme.textColor,
                                        )
                                    }
                                }
                            }
                        }
                    }
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

private fun calculateSlotHeights(
    availableHeight: Dp,
    durations: List<Long>,
    minHeight: Dp,
): List<Dp> {
    if (durations.isEmpty()) return emptyList()

    val minimumTotalHeight = minHeight.value * durations.size
    if (availableHeight.value < minimumTotalHeight) {
        return List(durations.size) { minHeight }
    }

    val heights = MutableList(durations.size) { 0f }
    val flexibleSlots = durations.indices.toMutableSet()
    var remainingHeight = availableHeight.value

    while (flexibleSlots.isNotEmpty()) {
        val remainingDuration = flexibleSlots
            .sumOf { durations[it].toDouble() }
            .toFloat()

        val slotsBelowMinimum = flexibleSlots.filter { index ->
            val proportionalHeight = remainingHeight *
                    (durations[index].toFloat() / remainingDuration)
            proportionalHeight < minHeight.value
        }

        if (slotsBelowMinimum.isEmpty()) {
            flexibleSlots.forEach { index ->
                heights[index] = remainingHeight *
                        (durations[index].toFloat() / remainingDuration)
            }
            break
        }

        slotsBelowMinimum.forEach { index ->
            heights[index] = minHeight.value
            remainingHeight -= minHeight.value
            flexibleSlots.remove(index)
        }
    }

    return heights.map { it.dp }
}