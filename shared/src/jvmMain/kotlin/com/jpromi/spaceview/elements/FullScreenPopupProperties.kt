package com.jpromi.spaceview.elements

import androidx.compose.ui.window.PopupProperties

internal actual fun fullScreenPopupProperties() = PopupProperties(
    focusable = true,
    usePlatformDefaultWidth = false,
    usePlatformInsets = false,
)
