package com.jpromi.spaceview.controllers

expect class FullscreenController {
    constructor()

    companion object {
        fun setFullscreen(enabled: Boolean)
        fun isFullscreenSupported(): Boolean
        //Needed?
        //fun getFullscreen(): Boolean
    }
}
