package com.jpromi.spaceview.network

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

fun ApiResult.Error.toUserMessage(): String = when (this) {
    is ApiResult.InvalidRequest -> message
    ApiResult.Unauthorized -> "Der Access Token ist ungültig."
    ApiResult.Forbidden -> "Keine Berechtigung für diese Anfrage."
    ApiResult.NotFound -> "Der angeforderte API-Endpunkt wurde nicht gefunden."
    is ApiResult.HttpError -> "Serverfehler $statusCode: $message"
    is ApiResult.NetworkError -> cause.message ?: "Server nicht erreichbar."
}