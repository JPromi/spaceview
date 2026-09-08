package com.jpromi.spaceview.services

import com.jpromi.spaceview.network.ApiResult
import com.jpromi.spaceview.network.executeRequest
import com.jpromi.spaceview.network.toHttpBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.request
import io.ktor.http.ContentType

class NextcloudService {

    suspend fun getNextcloudRootUrl(url: String): ApiResult<String?> =
        executeNextcloudRequest(
            expectSuccess = false
        ) { client ->
            val normalizedUrl = url.toHttpBaseUrl()
            if (normalizedUrl.hasPathAfterHost()) {
                return@executeNextcloudRequest normalizedUrl
            }

            val response = client.get(normalizedUrl) {
                accept(ContentType.Text.Html)
            }
            val loginUrl = response.request.url.toString()

            normalizedUrl
                .resolveRedirectLocation(loginUrl)
                .toNextcloudRootUrl()
        }

    suspend fun getNextcloudPage(url: String): ApiResult<String> =
        executeNextcloudRequest { client ->
            client.get(url.toHttpBaseUrl()).body<String>()
        }

    private suspend fun <T> executeNextcloudRequest(
        expectSuccess: Boolean = true,
        request: suspend (HttpClient) -> T
    ): ApiResult<T> =
        executeRequest(
            invalidRequestMessage = "",
            isRequestValid = { true },
            expectSuccess = expectSuccess,
            request = request,
        )

    private fun String.hasPathAfterHost(): Boolean {
        val protocolSeparatorIndex = indexOf("://")
        if (protocolSeparatorIndex == -1) {
            return false
        }

        val pathStartIndex = indexOf('/', startIndex = protocolSeparatorIndex + 3)

        return pathStartIndex != -1
    }

    private fun String.toNextcloudRootUrl(): String {
        val normalizedUrl = trim()
            .substringBefore('?')
            .substringBefore('#')
            .trimEnd('/')

        return when {
            normalizedUrl.endsWith("/login") ->
                normalizedUrl.removeSuffix("/login")
            else -> normalizedUrl
        }
    }

    private fun String.resolveRedirectLocation(location: String): String {
        val trimmedLocation = location.trim()

        if (trimmedLocation.startsWith("http://") || trimmedLocation.startsWith("https://")) {
            return trimmedLocation
        }

        val protocolSeparatorIndex = indexOf("://")
        if (protocolSeparatorIndex == -1) {
            return this
        }

        val pathStartIndex = indexOf('/', startIndex = protocolSeparatorIndex + 3)
        val origin = if (pathStartIndex == -1) this else substring(0, pathStartIndex)

        if (trimmedLocation.startsWith("/")) {
            return "$origin$trimmedLocation"
        }

        val baseUrl = if (endsWith("/")) this else substringBeforeLast("/", missingDelimiterValue = this)

        return "$baseUrl/$trimmedLocation"
    }
}
