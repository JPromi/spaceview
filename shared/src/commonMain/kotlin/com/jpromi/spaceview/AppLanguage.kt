package com.jpromi.spaceview

import androidx.compose.runtime.Composable

/** Applies the saved locale to both Compose resources and suspend resource lookups. */
@Composable
expect fun AppLanguage(language: String, content: @Composable () -> Unit)
