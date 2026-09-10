package com.jpromi.spaceview.services

import com.jpromi.spaceview.network.ApiResult
import com.jpromi.spaceview.network.executeRequest
import com.jpromi.spaceview.network.toHttpBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

class NextcloudService {

    private companion object {
        const val MAX_REDIRECTS = 5
    }

    suspend fun getNextcloudRootUrl(url: String): ApiResult<String?> =
        executeNextcloudRequest(
            expectSuccess = false,
            followRedirects = false,
        ) { client ->
            val normalizedUrl = url.toHttpBaseUrl()
            if (normalizedUrl.hasPathAfterHost()) {
                return@executeNextcloudRequest normalizedUrl
            }

            var currentUrl = normalizedUrl

            repeat(MAX_REDIRECTS) {
                val response = client.get(currentUrl) {
                    accept(ContentType.Text.Html)
                }

                val location = response.headers[HttpHeaders.Location]
                    ?: return@executeNextcloudRequest currentUrl.toRedirectCheckUrl().removeLoginPath()

                currentUrl = currentUrl.resolveRedirectLocation(location)
                if (currentUrl.isLoginUrl()) {
                    return@executeNextcloudRequest currentUrl.removeLoginPath()
                }
            }

            currentUrl.toRedirectCheckUrl().removeLoginPath()
        }

    suspend fun getNextcloudPage(url: String): ApiResult<String> =
        executeNextcloudRequest(expectSuccess = false) { client ->
            client.get(url.toHttpBaseUrl()) {
                accept(ContentType.Text.Html)
            }.bodyAsText()
        }

    private suspend fun <T> executeNextcloudRequest(
        expectSuccess: Boolean = true,
        followRedirects: Boolean = true,
        request: suspend (HttpClient) -> T
    ): ApiResult<T> =
        executeRequest(
            invalidRequestMessage = "",
            isRequestValid = { true },
            expectSuccess = expectSuccess,
            followRedirects = followRedirects,
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

    private fun String.toRedirectCheckUrl(): String =
        trim()
            .substringBefore('?')
            .substringBefore('#')
            .trimEnd('/')

    private fun String.isLoginUrl(): Boolean =
        toRedirectCheckUrl().endsWith("/login")

    private fun String.removeLoginPath(): String =
        toRedirectCheckUrl().removeSuffix("/login")

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
