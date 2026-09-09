package com.jpromi.spaceview.services.impl

import androidx.compose.ui.graphics.Color
import com.jpromi.spaceview.network.ApiResult
import com.jpromi.spaceview.network.toHttpBaseUrl
import com.jpromi.spaceview.services.NextcloudService
import com.jpromi.spaceview.services.ThemingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NextcloudThemingService(
    baseUrl: String,
    private val nextcloudService: NextcloudService = NextcloudService(),
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : ThemingService {

    private var resolvedBaseUrl: String = baseUrl.toHttpBaseUrl()

    override var baseUrl: String
        get() = resolvedBaseUrl
        set(value) {
            resolvedBaseUrl = value.toHttpBaseUrl()
        }

    override suspend fun getThemeColor(): Color? {
        val nextcloudBaseUrl = resolveNextcloudBaseUrl()
        val htmlBody = nextcloudService.getNextcloudPage("$nextcloudBaseUrl/SPACEVIEW_FORCE_ERROR")

        if (htmlBody is ApiResult.Success) {
            val regex = Regex("""<meta\s+name=["']theme-color["']\s+content=["'](#[0-9a-fA-F]{6})["']""")
            val match = regex.find(htmlBody.data)
            val hexColor = match?.groupValues?.getOrNull(1)

            if (hexColor != null) {
                return Color(
                    red = hexColor.substring(1, 3).toInt(16),
                    green = hexColor.substring(3, 5).toInt(16),
                    blue = hexColor.substring(5, 7).toInt(16),
                )
            }
        }

        return null
    }

    override suspend fun getLogo(): String? {
        return "${resolvedBaseUrl.toHttpBaseUrl()}/apps/theming/image/logo"
    }

    override suspend fun getBackgroundImage(): String? {
        return "${resolvedBaseUrl.toHttpBaseUrl()}/apps/theming/image/background"
    }

    private suspend fun resolveNextcloudBaseUrl(): String {
        return when (val result = nextcloudService.getNextcloudRootUrl(resolvedBaseUrl)) {
            is ApiResult.Success -> result.data?.toHttpBaseUrl()?.also { resolvedBaseUrl = it } ?: resolvedBaseUrl
            is ApiResult.Error -> resolvedBaseUrl
        }
    }

}
