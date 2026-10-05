package com.jpromi.spaceview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun AppLanguage(language: String, content: @Composable () -> Unit) {
    remember(language) { updateLanguage(language) }
    content()
}

private fun updateLanguage(language: String) {
    js("""
        if (!window.__spaceviewOriginalLanguages) {
            window.__spaceviewOriginalLanguages = Object.getOwnPropertyDescriptor(Navigator.prototype, "languages");
            Object.defineProperty(Navigator.prototype, "languages", {
                configurable: true,
                get: function() {
                    return window.__spaceviewLanguage
                        ? [window.__spaceviewLanguage]
                        : window.__spaceviewOriginalLanguages.get.call(this);
                }
            });
        }
        if (window.__spaceviewLanguage !== language) {
            window.__spaceviewLanguage = language;
            window.dispatchEvent(new Event("languagechange"));
        }
    """)
}
