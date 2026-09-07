package com.jpromi.spaceview

class AndroidPlatform : Platform {
    override val name: String = "iOS"
}

actual fun getPlatform(): Platform = AndroidPlatform()