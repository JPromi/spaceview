package com.jpromi.spaceview.util

import com.jpromi.spaceview.BuildKonfig

actual fun appVersion() =
    "${BuildKonfig.APP_VERSION} (${BuildKonfig.IOS_BUILD_NUMBER})"
