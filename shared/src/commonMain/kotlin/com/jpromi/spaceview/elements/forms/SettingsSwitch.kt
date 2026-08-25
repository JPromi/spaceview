package com.jpromi.spaceview.elements.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.jpromi.spaceview.AppTheme

@Composable
fun SettingsSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    text: String,
    enabled: Boolean = true,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                uncheckedBorderColor = AppTheme.borderSettings,
                checkedTrackColor = AppTheme.primary,
                uncheckedTrackColor = AppTheme.background,
                disabledUncheckedTrackColor = AppTheme.background.copy(alpha = .10f),
                disabledUncheckedBorderColor = AppTheme.borderSettings.copy(alpha = .10f),
                disabledCheckedTrackColor=  AppTheme.background.copy(alpha = .10f),
                disabledCheckedBorderColor = AppTheme.borderSettings.copy(alpha = .10f),
            ),
            enabled = enabled
        )
        Text(text, color = AppTheme.textColor)
    }
}