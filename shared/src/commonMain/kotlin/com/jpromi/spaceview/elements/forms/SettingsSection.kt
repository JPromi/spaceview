package com.jpromi.spaceview.elements.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jpromi.spaceview.AppTheme

@Composable
fun SettingsSection(
    title: String,
    transparentBackground : Boolean = false,
            content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if(transparentBackground)  Color.Transparent else AppTheme.backgroundSettings, shape = RoundedCornerShape(12.dp))
            .border(.5.dp, AppTheme.borderSettings, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = AppTheme.textColor,
            fontWeight = FontWeight.SemiBold,
        )

        content()
    }
}