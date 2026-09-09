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
        val htmlBody = nextcloudService.getNextcloudPage("$baseUrl/SPACEVIEW_FORCE_ERROR")

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
        val requestUrl = "${baseUrl.removeIndexPhp()}/core/css/guest.scss"
        val defaultCss = nextcloudService.getNextcloudPage(requestUrl)

        if (defaultCss is ApiResult.Success) {
            // check variable
            val varRegex = Regex("""--image-logo\s*:\s*url\(\s*['"]?([^'")]+)['"]?\s*\)""")
            var match = varRegex.find(defaultCss.data)

            if (match == null) {
                // check default logo
                val defaultRegex = Regex("""background-image\s*:\s*var\(\s*--image-logo\s*,\s*url\(\s*['"]?([^'")]+)['"]?\s*\)\s*\)""")
                match = defaultRegex.find(defaultCss.data)
            }

            return getNextcloudImageUrl(match?.groupValues[1], requestUrl)
        }

        return null;

        // Not working if the logo is set in a custom Theme
        // return "${resolvedBaseUrl.toHttpBaseUrl()}/apps/theming/image/logo"
    }

    override suspend fun getBackgroundImage(): String? {
        // ToDo: is not workling with every installation, sometimes it can be under another url, check for it the css

        val defaultCss = nextcloudService.getNextcloudPage("$baseUrl/apps/theming/theme/default.css")

        if (defaultCss is ApiResult.Success) {
            // get
            val regex = Regex("""--image-background\s*:\s*url\(\s*['"]?([^'")]+)['"]?\s*\)""")
            val match = regex.find(defaultCss.data)

            return getNextcloudImageUrl(match?.groupValues[1])
        }

        return null;

        // Not working if the background is set in a custom Theme
        // return "${resolvedBaseUrl.toHttpBaseUrl()}/apps/theming/image/background"
    }

    private fun getNextcloudImageUrl(path: String?, requestUrl: String? = null): String? {
        var _baseUrl = baseUrl
        if (requestUrl != null) {
            _baseUrl = requestUrl.substringBeforeLast('/') + "/"
        } else {
            _baseUrl = baseUrl
        }
        if (path != null) {
            if (path.startsWith("http")) {
                return path
            }

            val lastPart = path.substringAfterLast('/')

            return if ('.' in lastPart) {
                "${_baseUrl.removeIndexPhp()}$path"
            } else {
                "$_baseUrl$path"
            }
        }

        return null;
    }

    private fun String.removeIndexPhp(): String {
        if (this.endsWith("/index.php")) {
            return this.removeSuffix("/index.php")
        } else {
            return this
        }
    }

}
