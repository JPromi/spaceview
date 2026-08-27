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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jpromi.spaceview.AppTheme
import com.jpromi.spaceview.dtos.roomvox.toRoomVoxLocalDateTimeOrNull
import com.jpromi.spaceview.enums.SlotStatus
import com.jpromi.spaceview.models.Room
import com.jpromi.spaceview.models.RoomUse
import com.jpromi.spaceview.util.toMinuteOfDay
import com.jpromi.spaceview.util.toTimeText
import kotlin.collections.filter
import kotlin.collections.maxBy
import kotlin.collections.minByOrNull
import kotlin.collections.orEmpty

@Composable
fun NameStatusView(room: Room?, roomUse: RoomUse?, currentMinuteOfDay: Int) {
    // name
    Text(
        text = room?.name ?: "",
        modifier = Modifier.padding(bottom = 4.dp),
        fontWeight = FontWeight.W500,
        fontSize = 30.sp,
        color = AppTheme.textColor,
    )

    Spacer(modifier = Modifier.height(20.dp))

    val background = if (roomUse?.currentEvent != null) AppTheme.busyTagBackground else AppTheme.freeTagBackground
    // status
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = background,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        background.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                ),
                shape = RoundedCornerShape(12.dp),
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
                text = if (roomUse?.currentEvent != null) "Belegt" else "Frei",
                color = AppTheme.textColor,
                fontWeight = FontWeight.W700,
                fontSize = 32.sp,
                lineHeight = 10.sp,
            )

            if(roomUse?.currentEvent != null) {
                Text(
                    text = roomUse.currentEvent.title,
                    color = AppTheme.textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    lineHeight = 18.sp,
                )
            }

            var untilText : String?
            if(roomUse?.currentEvent != null) {
                val end = roomUse.currentEvent.end.toRoomVoxLocalDateTimeOrNull()
                untilText = "bis ${end?.toTimeText()} (${end?.toMinuteOfDay()?.minus(currentMinuteOfDay)} Minuten)"
            }
            else {
                untilText = roomUse?.slots.orEmpty().filter { slot ->
                    slot.start.toMinuteOfDay() > currentMinuteOfDay && slot.status == SlotStatus.BOOKED
                }.minByOrNull { slot -> slot.start.toMinuteOfDay() }?.end?.toTimeText()

                if (untilText == null)
                    untilText = roomUse?.slots.orEmpty()
                        .maxBy { slot -> slot.end.toMinuteOfDay() }.end.toTimeText()
                untilText = "bis ${untilText}"
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