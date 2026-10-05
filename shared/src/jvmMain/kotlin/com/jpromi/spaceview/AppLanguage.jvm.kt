package com.jpromi.spaceview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.util.Locale

private val systemLocale = Locale.getDefault()

@Composable
actual fun AppLanguage(language: String, content: @Composable () -> Unit) {
    remember(language) {
        Locale.setDefault(if (language.isEmpty()) systemLocale else Locale.forLanguageTag(language))
    }
    content()
}
