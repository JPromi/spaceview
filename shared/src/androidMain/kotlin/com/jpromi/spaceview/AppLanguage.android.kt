package com.jpromi.spaceview

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

private val systemLocale = Locale.getDefault()

@Composable
actual fun AppLanguage(language: String, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val current = LocalConfiguration.current
    val configuration = remember(language, current) {
        val locale = if (language.isEmpty()) systemLocale else Locale.forLanguageTag(language)
        Locale.setDefault(locale)
        Configuration(current).apply { setLocale(locale) }.also {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(it, context.resources.displayMetrics)
        }
    }
    CompositionLocalProvider(LocalConfiguration provides configuration, content = content)
}
