package com.jpromi.spaceview.network

sealed interface ServerConnectionResult {
    val message: String

    data class Success(
        override val message: String,
    ) : ServerConnectionResult

    data class Error(
        override val message: String,
    ) : ServerConnectionResult
}