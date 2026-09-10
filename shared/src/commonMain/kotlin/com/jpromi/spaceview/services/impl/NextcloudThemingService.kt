package com.jpromi.spaceview.services.impl

import androidx.compose.ui.graphics.Color
import com.jpromi.spaceview.enums.AssetSourceType
import com.jpromi.spaceview.models.Image
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
        var requestUrl = "${baseUrl}/apps/theming/theme/dark.css"
        val darkCss = nextcloudService.getNextcloudPage(requestUrl)

        if (darkCss is ApiResult.Success) {
            val defaultRegex = Regex("""background-image\s*:\s*var\(\s*--image-logo\s*,\s*url\(\s*['"]?([^'")]+)['"]?\s*\)\s*\)""")
            val varRegex = Regex("""--image-logo\s*:\s*url\(\s*['"]?([^'")]+)['"]?\s*\)""")

            // var
            var match = varRegex.find(darkCss.data)

            if (match == null) {
                // default fallback
                requestUrl = "${baseUrl.removeIndexPhp()}/core/css/guest.css"
                val guestCss = nextcloudService.getNextcloudPage(requestUrl)

                if (guestCss is ApiResult.Success) {
                    match = defaultRegex.find(guestCss.data)
                } else {
                    return null
                }
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

    override suspend fun getImageLibrary(): List<Image> {
        val requestUrl = "$baseUrl/SPACEVIEW_FORCE_ERROR"
        val htmlPage = nextcloudService.getNextcloudPage(requestUrl)

        val imageRegex = Regex(
            """(?:url\(\s*['"]?|src\s*=\s*['"]|href\s*=\s*['"])((?:[^"'()\s;{}<>]+?\.(?:png|jpe?g|webp|svg|avif|bmp|ico)(?:[?#][^"'()\s;{}<>]*)?)|(?:[^"'()\s;{}<>]+?/(?:logo|background)(?:[?#][^"'()\s;{}<>]*)?))""",
            RegexOption.IGNORE_CASE
                )

        val regexGetAllCssFiles = Regex(
            """<link\b(?=[^>]*\brel\s*=\s*["'][^"']*\bstylesheet\b[^"']*["'])[^>]*\bhref\s*=\s*["']([^"']+)["'][^>]*>""",
            RegexOption.IGNORE_CASE
        )

        val imageFiles = mutableListOf<Image>()
        val cssFiles = mutableListOf<String>()

        fun extractImages(
            content: String,
            sourceUrl: String
        ) {
            imageFiles += imageRegex.findAll(content)
                .mapNotNull { match ->
                    val imageUrl = getNextcloudImageUrl(
                        path = match.groupValues[1],
                        requestUrl = sourceUrl
                    )

                    if (isProbablyUiIcon(match.groupValues[1])) {
                        return@mapNotNull null
                    }

                    imageUrl?.let {
                        Image(
                            sourceType = AssetSourceType.REMOTE,
                            description = "Nextcloud",
                            copyright = "Your Nextcloud Server",
                            path = it
                        )
                    }
                }
                .toList()
        }

        if (htmlPage is ApiResult.Success) {
            // extract from HTML
            extractImages(
                content = htmlPage.data,
                sourceUrl = requestUrl
            )

            // extract CSS Files
            cssFiles += regexGetAllCssFiles.findAll(htmlPage.data)
                .map { it.groupValues[1] }
                .toList()

            // Load CSS and search for images
            for (cssFile in cssFiles) {
                val cssUrl = getNextcloudImageUrl(
                    path = cssFile,
                    requestUrl = requestUrl
                ) ?: continue

                val cssContent = nextcloudService.getNextcloudPage(cssUrl)

                if (cssContent is ApiResult.Success) {
                    extractImages(
                        content = cssContent.data,
                        sourceUrl = cssUrl
                    )
                }
            }
        }

        return imageFiles
            .distinctBy { it.path }
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

            if (path.startsWith("/")) {
                _baseUrl = _baseUrl
                    .substringBefore("://")
                    .let { scheme ->
                        val rest = _baseUrl.substringAfter("://")
                        "$scheme://${rest.substringBefore('/')}"
                    }
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

    private fun isProbablyUiIcon(path: String): Boolean {
        val clean = path
            .substringBefore('?')
            .lowercase()

        val fileName = clean.substringAfterLast('/')

        return clean.contains("/img/actions/") ||
                clean.contains("/img/filetypes/") ||
                fileName.startsWith("favicon") ||
                fileName.startsWith("loading") ||
                fileName == "breadcrumb.svg" ||
                fileName.startsWith("checkbox-") ||
                fileName.startsWith("checkmark-") ||
                fileName.startsWith("caret-") ||
                fileName.startsWith("confirm") ||
                fileName.startsWith("error-") ||
                fileName.startsWith("info-")
    }

    private fun String.removeIndexPhp(): String {
        if (this.endsWith("/index.php")) {
            return this.removeSuffix("/index.php")
        } else {
            return this
        }
    }

}
