package com.jpromi.spaceview.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.jpromi.spaceview.AppTheme
import com.jpromi.spaceview.AppSettings

@Composable
fun AdminPinPopup(
    onDismiss: () -> Unit,
    onValidPinEnteredFunction: () -> Unit,
    appSettings: AppSettings,
) {
    val pinLength = 4
    var pinState by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Popup(
        onDismissRequest = onDismiss,
        alignment = Alignment.Center,
        properties = PopupProperties(focusable = true),
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = AppTheme.background,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Admin PIN",
                    color = AppTheme.textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                BasicTextField(
                    value = pinState,
                    onValueChange = { value ->
                        val sanitizedValue = value
                            .filter { it.isDigit() }
                            .take(pinLength)

                        pinState = sanitizedValue
                        hasError = false

                        if (sanitizedValue.length == pinLength) {
                            if (sanitizedValue == appSettings.adminPin) {
                                onDismiss()
                                onValidPinEnteredFunction()
                            } else {
                                hasError = true
                                pinState = ""
                            }
                        }
                    },
                    modifier = Modifier
                        .width(292.dp)
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(
                        color = Color.Transparent,
                        fontSize = 1.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    singleLine = true,
                    decorationBox = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(pinLength) { index ->
                                val hasDigit = pinState.getOrNull(index) != null
                                val borderColor = if (hasError) Color(0xFFFF6B6B) else AppTheme.textColor

                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            color = Color(0xFF2B2B2B),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = borderColor,
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (hasDigit) "*" else "",
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                )

                if (hasError) {
                    Text(
                        text = "Falscher PIN",
                        color = Color(0xFFFF6B6B),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
