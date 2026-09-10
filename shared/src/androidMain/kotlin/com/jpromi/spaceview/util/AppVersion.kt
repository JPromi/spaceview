package com.jpromi.spaceview.util

import com.jpromi.spaceview.BuildKonfig

actual fun appVersion() =
    "${BuildKonfig.ANDROID_VERSION} (${BuildKonfig.ANDROID_VERSION_CODE})"
