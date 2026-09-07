package com.jpromi.spaceview.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberPopupState(): PopupState = remember { PopupState() }

class PopupState internal constructor() {
    var isVisible by mutableStateOf(false)
        private set

    fun open() {
        isVisible = true
    }

    fun close() {
        isVisible = false
    }
}
