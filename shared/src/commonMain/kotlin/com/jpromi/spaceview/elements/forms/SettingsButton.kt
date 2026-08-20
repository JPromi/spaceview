package com.jpromi.spaceview.elements.forms

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jpromi.spaceview.AppColor

@Composable
fun SettingsButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    enabled: Boolean = true,
) {
    SettingsButton(
        onClick = onClick,
        modifier = modifier,
        isPrimary = isPrimary,
        enabled = enabled,
    ) {
        Text(text = text)
    }
}

@Composable
fun SettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    val borderColor = if (enabled) {
        AppColor.primary
    } else {
        Color.Transparent
    }

    val buttonColors = if (isPrimary) {
        ButtonDefaults.buttonColors(
            containerColor = AppColor.primary,
            contentColor = AppColor.textColor,
            disabledContainerColor = AppColor.primary.copy(alpha = .45f),
            disabledContentColor = AppColor.textColor.copy(alpha = .5f),
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = AppColor.primary.copy(alpha = .20f),
            contentColor = AppColor.textColor,
            disabledContainerColor = AppColor.primary.copy(alpha = .10f),
            disabledContentColor = AppColor.textColor.copy(alpha = .5f),
        )
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(.5.dp, borderColor, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = buttonColors,

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}
