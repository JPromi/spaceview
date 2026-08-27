package com.jpromi.spaceview.elements.roomscreen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import com.jpromi.spaceview.AppTheme

@Composable
fun RoundIconButton(onClick: () -> Unit, icon : ImageVector)
{
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .border(
                width = 1.dp,
                color = AppTheme.textColor,
                shape = CircleShape
            ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp),
        )
    }
}