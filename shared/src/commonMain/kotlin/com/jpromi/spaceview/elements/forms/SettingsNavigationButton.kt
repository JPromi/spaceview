package com.jpromi.spaceview.elements.forms

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.jpromi.spaceview.AppTheme

@Composable
fun SettingsNavigationButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    icon: ImageVector? = null,
) {
    SettingsNavigationButton(
        onClick = onClick,
        modifier = modifier,
        isActive = isActive,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(text = text)
    }
}

@Composable
fun SettingsNavigationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val buttonColors = if (isActive) {
        ButtonDefaults.buttonColors(
            containerColor = AppTheme.primary.copy(alpha = .20f),
            contentColor = AppTheme.textColor,
            disabledContainerColor = AppTheme.primary.copy(alpha = .10f),
            disabledContentColor = AppTheme.textColor.copy(alpha = .5f),
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = AppTheme.primary.copy(alpha = 0f),
            contentColor = AppTheme.textColor,
            disabledContainerColor = AppTheme.primary.copy(alpha = .10f),
            disabledContentColor = AppTheme.textColor.copy(alpha = .5f),
        )
    }

    val borderColor = if (isActive) {
        AppTheme.primary
    } else {
        AppTheme.primary.copy(alpha = 0f)
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(.5.dp, borderColor, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = buttonColors,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}
