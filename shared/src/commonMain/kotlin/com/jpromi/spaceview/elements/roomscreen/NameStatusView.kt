package com.jpromi.spaceview.elements.roomscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import com.jpromi.spaceview.AppTheme
import com.jpromi.spaceview.dtos.roomvox.toRoomVoxLocalDateTimeOrNull
import com.jpromi.spaceview.enums.SlotStatus
import com.jpromi.spaceview.models.Room
import com.jpromi.spaceview.models.RoomUse
import com.jpromi.spaceview.util.toMinuteOfDay
import com.jpromi.spaceview.util.toTimeText
import org.jetbrains.compose.resources.stringResource
import spaceview.shared.generated.resources.Res
import spaceview.shared.generated.resources.roomview_booking_booked
import spaceview.shared.generated.resources.roomview_booking_free
import spaceview.shared.generated.resources.roomview_booking_unknown

@Composable
fun NameStatusView(roomUse: RoomUse?, currentMinuteOfDay: Int, hazeState: HazeState, withBlurEffect: Boolean = false) {
    val boxShape = RoundedCornerShape(12.dp)
    var boxModifier = Modifier
        .clip(RoundedCornerShape(12.dp));

    val background = if (roomUse?.currentEvent != null) AppTheme.busyTagBackground else AppTheme.freeTagBackground

    if (withBlurEffect) {
        boxModifier = boxModifier.hazeEffect(hazeState) {
            blurRadius = 24.dp
        }
    }

    // status
    Box(
        modifier = boxModifier
            .border(
                width = 1.dp,
                color = background,
                shape = boxShape
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        background.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                ),
            )
            .padding(12.dp)
            .height(100.dp)
            .fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .padding(start = 6.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = when {
                    roomUse == null || roomUse.slots.isEmpty() -> stringResource(Res.string.roomview_booking_unknown)
                    roomUse.currentEvent != null -> stringResource(Res.string.roomview_booking_booked)
                    else -> stringResource(Res.string.roomview_booking_free)
                },
                color = AppTheme.textColor,
                fontWeight = FontWeight.W700,
                fontSize = 32.sp,
                lineHeight = 10.sp,
            )

            if(roomUse?.currentEvent != null) {
                Text(
                    text = roomUse.currentEvent.title ?: "",
                    color = AppTheme.textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    lineHeight = 18.sp,
                )
            }

            val untilText = if (roomUse?.currentEvent != null) {
                val end = roomUse.currentEvent.end.toRoomVoxLocalDateTimeOrNull()
                end?.let {
                    "bis ${it.toTimeText()} (${it.toMinuteOfDay() - currentMinuteOfDay} Minuten)"
                } ?: "Endzeit unbekannt"
            } else {
                val slots = roomUse?.slots.orEmpty()
                val nextBookingStart = slots
                    .filter { it.start.toMinuteOfDay() > currentMinuteOfDay && it.status == SlotStatus.BOOKED }
                    .minByOrNull { it.start }
                    ?.start
                val freeUntil = nextBookingStart ?: slots.maxByOrNull { it.end }?.end
                freeUntil?.let { "bis ${it.toTimeText()}" } ?: "Keine Verfügbarkeitsdaten"
            }

            Text(
                text = untilText,
                color = AppTheme.textColor,
                fontSize = 18.sp,
                lineHeight = 18.sp,
            )
        }
    }
}
