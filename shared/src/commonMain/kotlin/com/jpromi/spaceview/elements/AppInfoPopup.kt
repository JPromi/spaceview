package com.jpromi.spaceview.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.jpromi.spaceview.AppTheme
import com.jpromi.spaceview.BuildKonfig

@Composable
fun AppInfoPopup(
    onDismiss: () -> Unit
) {

    Popup(
        onDismissRequest = onDismiss,
        alignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x75000000))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDismiss
                ),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {}
                    )
                    .background(
                        color = AppTheme.background,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .fillMaxWidth(0.8f)
                    .padding(24.dp),
            ) {

                // Title
                Row {
                    // ToDo: Logo
                    Column {
                        Text(
                            text = "Space View",
                            color = AppTheme.textColor,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Version: ${BuildKonfig.APP_VERSION}",
                            color = AppTheme.textColor.copy(alpha = 0.5f),
                            fontSize = 16.sp,
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                )

                // Description
                Text(
                    text = "Beschreibung",
                    color = AppTheme.textColor.copy(alpha = 0.5f),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Space View ist... Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
                    color = AppTheme.textColor,
                )
            }
        }
    }
}
