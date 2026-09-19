package com.jpromi.spaceview

import web.navigator.navigator

class JsPlatform : Platform {
    override val name: String = "WEB"
}

actual fun getPlatform(): Platform = JsPlatform()