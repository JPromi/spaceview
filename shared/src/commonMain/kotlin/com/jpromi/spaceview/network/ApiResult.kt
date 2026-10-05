package com.jpromi.spaceview.network

import org.jetbrains.compose.resources.getString
import spaceview.shared.generated.resources.Res
import spaceview.shared.generated.resources.network_forbidden_text
import spaceview.shared.generated.resources.network_http_error_text
import spaceview.shared.generated.resources.network_network_error_text_fallback
import spaceview.shared.generated.resources.network_not_found_text
import spaceview.shared.generated.resources.network_unauthorized_text

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>

    sealed interface Error : ApiResult<Nothing>

    data class InvalidRequest(val message: String) : Error
    data object Unauthorized : Error
    data object Forbidden : Error
    data object NotFound : Error
    data class HttpError(
        val statusCode: Int,
        val message: String,
    ) : Error
    data class NetworkError(val cause: Throwable) : Error
}

suspend fun ApiResult.Error.toUserMessage(): String = when (this) {
    is ApiResult.InvalidRequest -> message
    ApiResult.Unauthorized -> getString(Res.string.network_unauthorized_text)
    ApiResult.Forbidden -> getString(Res.string.network_forbidden_text)
    ApiResult.NotFound -> getString(Res.string.network_not_found_text)
    is ApiResult.HttpError -> getString(Res.string.network_http_error_text, statusCode, message)
    is ApiResult.NetworkError -> cause.message ?: getString(Res.string.network_network_error_text_fallback)
}