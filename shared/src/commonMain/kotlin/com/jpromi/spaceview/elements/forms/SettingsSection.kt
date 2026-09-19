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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import com.jpromi.spaceview.AppTheme

@Composable
fun SettingsSection(
    title: String,
    transparentBackground: Boolean = false,
    hazeState: HazeState? = null,
    withBlur: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sectionShape = RoundedCornerShape(12.dp)
    var sectionModifier = Modifier
        .fillMaxWidth()
        .clip(sectionShape)

    sectionModifier = if (withBlur && hazeState != null) {
        sectionModifier.hazeEffect(hazeState) {
            blurRadius = 20.dp
            blurredEdgeTreatment = BlurredEdgeTreatment(sectionShape)
        }
    } else {
        sectionModifier.background(
            color = if (transparentBackground) Color.Transparent else AppTheme.backgroundSettings,
            shape = sectionShape,
        )
    }

    Column(
        modifier = sectionModifier
            .border(.5.dp, AppTheme.borderSettings, shape = sectionShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = AppTheme.textColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
        )

        content()
    }
}
