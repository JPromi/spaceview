package com.jpromi.spaceview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSUserDefaults

@Composable
actual fun AppLanguage(language: String, content: @Composable () -> Unit) {
    remember(language) {
        if (language.isEmpty()) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey("AppleLanguages")
        } else {
            NSUserDefaults.standardUserDefaults.setObject(listOf(language), "AppleLanguages")
        }
    }
    content()
}
