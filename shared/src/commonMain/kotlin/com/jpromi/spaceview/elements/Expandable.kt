package com.jpromi.spaceview.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowDown
import com.composables.icons.lucide.ArrowUp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ChevronUp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import com.jpromi.spaceview.AppTheme

@Composable
fun Expandable(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier
            .clickable { isExpanded = !isExpanded }
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(.5.dp, AppTheme.borderSettings, shape = RoundedCornerShape(12.dp))
            .background(color = AppTheme.backgroundSettings)

    ) {
        Row(
            modifier = modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, color = AppTheme.textColor)
            Icon(
                imageVector = if (isExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }

        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = isExpanded
        ) {
            content()
        }
    }
}