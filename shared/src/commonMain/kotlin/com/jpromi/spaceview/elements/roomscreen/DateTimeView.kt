package com.jpromi.spaceview.elements.roomscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jpromi.spaceview.AppTheme
import com.jpromi.spaceview.util.toDateText
import com.jpromi.spaceview.util.toTimeText
import kotlinx.datetime.LocalDateTime

@Composable
fun DateTimeView(currentDateTime: LocalDateTime) {
    val currentTimeText = currentDateTime.toTimeText()
    val currentDateText = currentDateTime.toDateText()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = currentTimeText,
            modifier = Modifier.padding(bottom = 4.dp),
            fontWeight = FontWeight.W500,
            fontSize = 42.sp,
            color = AppTheme.textColor,
        )
        Text(
            text = currentDateText,
            fontWeight = FontWeight.W400,
            fontSize = 20.sp,
            color = AppTheme.textColor,
        )

    }
}