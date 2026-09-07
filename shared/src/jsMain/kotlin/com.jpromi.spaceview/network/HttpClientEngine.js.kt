package com.jpromi.spaceview.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

actual fun platformHttpClientEngine(): HttpClientEngineFactory<*> = Js