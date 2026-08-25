package com.jpromi.spaceview.elements.forms

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Lucide
import com.jpromi.spaceview.AppTheme

@Composable
fun <T> SettingsDropdown(
    label: String,
    options: List<T>,
    selectedOption: T?,
    optionText: (T) -> String,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val selectedText = selectedOption?.let(optionText).orEmpty()

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = AppTheme.textColor,
            modifier = Modifier.padding(start = 5.dp)
        )

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val dropdownWidth = maxWidth

            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { isExpanded = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(.5.dp, AppTheme.borderSettings, shape = RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppTheme.background,
                        contentColor = AppTheme.textColor,
                        disabledContainerColor = AppTheme.background,
                        disabledContentColor = AppTheme.textColor.copy(alpha = .75f),
                    ),
                ) {
                    Text(selectedText)

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        imageVector = Lucide.ChevronDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }

                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    offset = DpOffset(x = 0.dp, y = 0.dp),
                    containerColor = Color.Transparent,
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .width(dropdownWidth),
                ) {
                    Surface(
                        modifier = Modifier
                            .width(dropdownWidth)
                            .border(.5.dp, AppTheme.borderSettings, shape = RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        color = AppTheme.background,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp,
                    ) {
                        Column {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = optionText(option),
                                            color = AppTheme.textColor,
                                        )
                                    },
                                    onClick = {
                                        onOptionSelected(option)
                                        isExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
