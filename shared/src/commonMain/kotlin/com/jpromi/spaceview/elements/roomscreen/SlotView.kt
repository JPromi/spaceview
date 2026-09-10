package com.jpromi.spaceview.elements.roomscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jpromi.spaceview.AppTheme
import com.jpromi.spaceview.enums.SlotStatus
import com.jpromi.spaceview.models.RoomUse
import com.jpromi.spaceview.util.toMinuteOfDay
import com.jpromi.spaceview.util.toTimeText
import kotlin.collections.filter
import kotlin.collections.forEachIndexed
import kotlin.collections.map
import kotlin.collections.orEmpty

@Composable
fun SlotView(roomUse: RoomUse?, currentMinuteOfDay: Int) {
    val slots = roomUse?.slots.orEmpty().filter { slot ->
        slot.end.toMinuteOfDay() > currentMinuteOfDay
    }
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
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
