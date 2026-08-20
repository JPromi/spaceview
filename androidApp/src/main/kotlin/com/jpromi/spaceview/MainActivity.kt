package com.jpromi.spaceview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.jpromi.spaceview.controllers.FullscreenController
import com.jpromi.spaceview.controllers.LocalFullscreenController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val holder = remember {
                object : FullscreenController {
                    override fun setFullscreen(enabled: Boolean) {
                        val controller = WindowCompat.getInsetsController(window, window.decorView)
                        if (enabled) {
                            controller.hide(WindowInsetsCompat.Type.systemBars())
                            controller.systemBarsBehavior =
                                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        } else {
                            controller.show(WindowInsetsCompat.Type.systemBars())
                        }
                    }
                }
            }

            CompositionLocalProvider(LocalFullscreenController provides holder) {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}