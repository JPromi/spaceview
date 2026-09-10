package com.jpromi.spaceview.elements

import androidx.compose.ui.window.PopupProperties

internal actual fun fullScreenPopupProperties() = PopupProperties(
    focusable = true,
    clippingEnabled = false,
    usePlatformDefaultWidth = false,
)
