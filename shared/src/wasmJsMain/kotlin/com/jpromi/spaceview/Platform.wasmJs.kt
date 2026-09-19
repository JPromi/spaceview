package com.jpromi.spaceview

class WasmPlatform : Platform {
    override val name: String = "WEB"
}

actual fun getPlatform(): Platform = WasmPlatform()