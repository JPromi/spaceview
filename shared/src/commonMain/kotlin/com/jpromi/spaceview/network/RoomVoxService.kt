package com.jpromi.spaceview.network

import com.jpromi.spaceview.AppSettings
import com.jpromi.spaceview.CalendarSettings
import com.jpromi.spaceview.dtos.roomvox.RVRoomAvailabilityDTO
import com.jpromi.spaceview.dtos.roomvox.RVRoomDTO
import com.jpromi.spaceview.dtos.roomvox.RVRoomStatusDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException

class RoomVoxService(
    private val appSettings: AppSettings = AppSettings(),
    private val calendarSettings: CalendarSettings = CalendarSettings(),
) {
    val baseUrl: String
        get() = calendarSettings.roomVoxServerUrl.toHttpBaseUrl() + "/apps/roomvox/api/v1"

    suspend fun getRooms(): ApiResult<List<RVRoomDTO>> = executeRequest { client ->
        client.get("$baseUrl/rooms") {
            addAuthorizationHeader()
        }.body()
    }

    suspend fun getRoom(roomId: String): ApiResult<RVRoomDTO> {
        if (roomId.isBlank()) {
            return ApiResult.InvalidRequest("Bitte zuerst einen Raum auswählen.")
        }

        return executeRequest { client ->
            client.get("$baseUrl/rooms/${roomId.trim()}") {
                addAuthorizationHeader()
            }.body()
        }
    }

    suspend fun getRoomStatus(roomId: String): ApiResult<RVRoomStatusDTO> {
        if (roomId.isBlank()) {
            return ApiResult.InvalidRequest("Bitte zuerst einen Raum auswählen.")
        }

        return executeRequest { client ->
            client.get("$baseUrl/rooms/${roomId.trim()}/status") {
                addAuthorizationHeader()
            }.body()
        }
    }

    suspend fun getRoomAvailability(roomId: String): ApiResult<RVRoomAvailabilityDTO> {
        if (roomId.isBlank()) {
            return ApiResult.InvalidRequest("Bitte zuerst einen Raum auswählen.")
        }

        return executeRequest { client ->
            client.get("$baseUrl/rooms/${roomId.trim()}/availability") {
                addAuthorizationHeader()
            }.body()
        }
    }

    private suspend fun <T> executeRequest(
        request: suspend (HttpClient) -> T,
    ): ApiResult<T> {
        if (calendarSettings.roomVoxServerUrl.isBlank()) {
            return ApiResult.InvalidRequest("Bitte Server-URL eingeben.")
        }

        val client = HttpClientFactory.create()
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

    private fun HttpRequestBuilder.addAuthorizationHeader() {
        if (calendarSettings.roomVoxAccessToken.isNotBlank()) {
            bearerAuth(calendarSettings.roomVoxAccessToken.trim())
        }
    }
}

fun String.toHttpBaseUrl(): String {
    val trimmedUrl = trim().trimEnd('/')

    if (trimmedUrl.isBlank()) {
        return ""
    }

    if (trimmedUrl.startsWith("http://") || trimmedUrl.startsWith("https://")) {
        return trimmedUrl
    }

    return "https://$trimmedUrl"
}