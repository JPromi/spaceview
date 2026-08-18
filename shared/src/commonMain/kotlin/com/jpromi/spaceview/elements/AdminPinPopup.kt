package com.jpromi.spaceview.elements


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.jpromi.spaceview.AppColor
import com.jpromi.spaceview.AppSettings

@Composable
fun AdminPinPopup(
    onDismiss: () -> Unit,
    onValidPinEnteredFunction: () -> Unit,
    appSettings: AppSettings = remember { AppSettings() },
) {
    var pinState by remember { mutableStateOf("") }
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
                .background(color = AppColor.background)
        ) {
            OutlinedTextField(
                value = pinState,
                onValueChange = {
                    pinState = it
                    if (pinState == appSettings.adminPin) {
                        onValidPinEnteredFunction()
                    }
                },
                modifier = Modifier.focusRequester(focusRequester),
                label = { Text("Admin PIN") },
                singleLine = true,
            )
        }
    }
}