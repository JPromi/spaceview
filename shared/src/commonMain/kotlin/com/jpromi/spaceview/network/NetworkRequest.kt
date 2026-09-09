package com.jpromi.spaceview.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException

suspend fun <T> executeRequest(
    invalidRequestMessage: String? = null,
    isRequestValid: () -> Boolean = { true },
    expectSuccess: Boolean = true,
    followRedirects: Boolean = true,
    request: suspend (HttpClient) -> T,
): ApiResult<T> {
    if (!isRequestValid()) {
        return ApiResult.InvalidRequest(invalidRequestMessage ?: "Ungueltige Anfrage.")
    }

    val client = HttpClientFactory.create(
        expectSuccess = expectSuccess,
        followRedirects = followRedirects,
    )
    return try {
        ApiResult.Success(request(client))
    } catch (error: ResponseException) {
        when (error.response.status) {
            HttpStatusCode.Unauthorized -> ApiResult.Unauthorized
            HttpStatusCode.Forbidden -> ApiResult.Forbidden
            HttpStatusCode.NotFound -> ApiResult.NotFound
            else -> ApiResult.HttpError(
                statusCode = error.response.status.value,
                message = error.message ?: error.response.status.description,
            )
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        ApiResult.NetworkError(error)
    } finally {
        client.close()
    }
}

fun String.toHttpBaseUrl(): String {
    val trimmedUrl = trim()
        .trimEnd('/')
        .substringBefore('?')
        .substringBefore('#')
        .trimEnd('/')

    if (trimmedUrl.isBlank()) {
        return ""
    }

    if (trimmedUrl.startsWith("http://") || trimmedUrl.startsWith("https://")) {
        return trimmedUrl
    }

    return "https://$trimmedUrl"
}
