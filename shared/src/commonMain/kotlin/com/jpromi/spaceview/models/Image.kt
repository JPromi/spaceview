package com.jpromi.spaceview.models

import com.jpromi.spaceview.enums.AssetSourceType
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val sourceType: AssetSourceType,
    val description: String? = null,
    val copyright: String? = null,
    val path: String? = null,
)
