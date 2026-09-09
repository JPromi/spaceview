package com.jpromi.spaceview.elements.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jpromi.spaceview.AppTheme

@Composable
fun SettingsColorButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Color.White,
    selected: Boolean = false,
) {
    Box(
        modifier = Modifier
            .then(
                if (selected) {
                    Modifier
                        .border(
                            width = 2.dp,
                            color = AppTheme.textColor,
                            shape = CircleShape
                        )
                } else {
                    Modifier
                }
            )
            .padding(4.dp)
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
            .clickable {
                onClick()
            }
    )
}