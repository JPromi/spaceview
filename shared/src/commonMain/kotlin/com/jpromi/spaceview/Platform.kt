package com.jpromi.spaceview

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform