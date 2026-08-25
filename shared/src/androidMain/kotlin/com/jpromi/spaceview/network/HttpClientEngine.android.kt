package com.jpromi.spaceview.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android

actual fun platformHttpClientEngine(): HttpClientEngineFactory<*> = Android