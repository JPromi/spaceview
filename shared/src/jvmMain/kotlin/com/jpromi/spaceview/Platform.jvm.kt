package com.jpromi.spaceview

class JVMPlatform : Platform {
    override val name: String = "JVM"
}

actual fun getPlatform(): Platform = JVMPlatform()