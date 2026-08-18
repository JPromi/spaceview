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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jpromi.spaceview.AppColor
import com.jpromi.spaceview.AppSettings
import com.jpromi.spaceview.elements.AdminPinPopup
import com.jpromi.spaceview.enums.SlotStatus
import com.jpromi.spaceview.models.Room
import com.jpromi.spaceview.models.RoomAvailability
import com.jpromi.spaceview.models.RoomStatus
import com.jpromi.spaceview.network.ApiResult
import com.jpromi.spaceview.network.RoomVoxService
import com.jpromi.spaceview.network.toUserMessage
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun RoomScreen(
    onOpenConfiguration: () -> Unit,
    appSettings: AppSettings = remember { AppSettings() },
    roomVoxService: RoomVoxService = remember { RoomVoxService(appSettings) },
) {
    var roomStatus by remember { mutableStateOf<RoomStatus?>(null) }
    var roomAvailability by remember { mutableStateOf<RoomAvailability?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoadingRoom by remember { mutableStateOf(true) }
    var isLoadingAvailability by remember { mutableStateOf(true) }
    var currentMinuteOfDay by remember { mutableStateOf(0) }
    var currentTimeText by remember { mutableStateOf("--:--") }
    var currentDateText by remember { mutableStateOf("--.--.----") }

    var isAdminPinPopupVisible by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        while (true) {
            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
            currentMinuteOfDay = now.hour * 60 + now.minute
            currentTimeText = "${now.hour.twoDigits()}:${now.minute.twoDigits()}"
            currentDateText = "${now.day.twoDigits()}.${(now.month.ordinal + 1).twoDigits()}.${now.year}"
            delay(30_000)
        }
    }

    LaunchedEffect(appSettings.selectedRoomId) {
        isLoadingRoom = true
        isLoadingAvailability = true

        while (true) {
            when (val result = roomVoxService.getRoomStatus(appSettings.selectedRoomId)) {
                is ApiResult.Success -> roomStatus = result.data
                is ApiResult.Error -> {
                    roomStatus = null
                    errorMessage = result.toUserMessage()
                }
            }
            isLoadingRoom = false

            when (val result = roomVoxService.getRoomAvailability(appSettings.selectedRoomId)) {
                is ApiResult.Success -> roomAvailability = result.data
                is ApiResult.Error -> {
                    roomAvailability = null
                    errorMessage = result.toUserMessage()
                }
            }
            isLoadingAvailability = false
            delay(30_000) // 30 seconds
        }
    }

    fun getWeightFromTime(startTime: String, endTime: String): Long {
        // time = "HH:mm"

        val startParts = startTime.split(":")
        val endParts = endTime.split(":")

        val startHour = startParts[0].toIntOrNull() ?: 0
        val startMinute = startParts[1].toIntOrNull() ?: 0

        val endHour = endParts[0].toIntOrNull() ?: 0
        val endMinute = endParts[1].toIntOrNull() ?: 0

        val startTotalMinutes = startHour * 60 + startMinute
        val endTotalMinutes = endHour * 60 + endMinute

        return (endTotalMinutes - startTotalMinutes).toLong()
    }

    // UI
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppColor.background)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.weight(4f).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Datetime
            Column (modifier = Modifier.weight(1f)) {
                Text(
                    text = currentTimeText,
                    modifier = Modifier.padding(bottom = 4.dp),
                    fontWeight = FontWeight.W500,
                    fontSize = 50.sp,
                    color = AppColor.textColor,
                )
                Text(
                    text = currentDateText,
                    fontWeight = FontWeight.W400,
                    fontSize = 20.sp,
                    color = AppColor.textColor,
                )
            }

            // Name & Status
            Column (
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = roomStatus?.room?.name ?: "",
                    modifier = Modifier.padding(bottom = 4.dp),
                    fontWeight = FontWeight.W500,
                    fontSize = 30.sp,
                    color = AppColor.textColor,
                )

                Spacer(modifier = Modifier.height(24.dp))


                // status
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = if (roomStatus?.currentBooking != null) {
                                AppColor.busyTagBackground
                            } else {
                                AppColor.freeTagBackground
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    if (roomStatus?.currentBooking != null) {
                                        AppColor.busyTagBackground
                                    } else {
                                        AppColor.freeTagBackground
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
                    if (roomStatus?.currentBooking == null) {
                        // busy
                        Column(
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Belegt",
                                color = AppColor.textColor,
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
                                color = AppColor.textColor,
                                fontWeight = FontWeight.W700,
                                fontSize = 32.sp,
                                lineHeight = 10.sp,
                            )
                            Text(
                                text = "bis xx:xx", // ToDo: Implement time until free
                                color = AppColor.textColor,
                                fontSize = 18.sp,
                                lineHeight = 18.sp,
                            )
                        }
                    }

                }
            }

            // Settings
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Button(
                    onClick = if (appSettings.adminPin.isNotEmpty()) {
                        { isAdminPinPopupVisible = true }
                    } else {
                        onOpenConfiguration
                    }
                ) {
                    Text("Einstellungen")
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(3f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(6.dp)) {

            // Slots
            Column (modifier = Modifier.fillMaxHeight().weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (isLoadingAvailability) {
                    CircularProgressIndicator()
                } else {
                    val slots = roomAvailability?.slots.orEmpty().filter { slot ->
                        timeToMinutes(slot.end) > currentMinuteOfDay
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
                            getWeightFromTime(it.start, it.end).coerceAtLeast(1L)
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
                                            color = AppColor.slotBackground,
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
                                                    color = if (slot.status == SlotStatus.busy) {
                                                        AppColor.busyTagBackground
                                                    } else {
                                                        AppColor.freeTagBackground
                                                    },
                                                    shape = RoundedCornerShape(6.dp)
                                                )
                                                .padding(vertical = 2.dp, horizontal = 6.dp)
                                        ) {
                                            Text(
                                                text = slot.status.toString(),
                                                color = if (slot.status == SlotStatus.busy) {
                                                    AppColor.busyTabTextColor
                                                } else {
                                                    AppColor.freeTabTextColor
                                                },
                                            )
                                        }

                                        // Time
                                        Text(
                                            text = "${slot.start} - ${slot.end}",
                                            color = AppColor.textColor,
                                        )
                                    }


                                    // Title
                                    Text(
                                        text = if (slot.status == SlotStatus.busy) {
                                            slot.title ?: "Belegt"
                                        } else {
                                            "Frei"
                                        },
                                        color = AppColor.textColor,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (appSettings.showAddEvent) {
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

    if (isAdminPinPopupVisible) {
        AdminPinPopup(
            onValidPinEnteredFunction = {
                onOpenConfiguration()
            },
            onDismiss = { isAdminPinPopupVisible = false }
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

private fun timeToMinutes(time: String): Int {
    val parts = time.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: return 0
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: return 0
    return hour * 60 + minute
}

private fun Int.twoDigits(): String = toString().padStart(2, '0')