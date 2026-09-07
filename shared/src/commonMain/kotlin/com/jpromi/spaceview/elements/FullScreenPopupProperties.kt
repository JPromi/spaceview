package com.jpromi.spaceview.elements

import androidx.compose.ui.window.PopupProperties

// Full-screen scrims must extend beyond platform safe-area bounds.
internal expect fun fullScreenPopupProperties(): PopupProperties
