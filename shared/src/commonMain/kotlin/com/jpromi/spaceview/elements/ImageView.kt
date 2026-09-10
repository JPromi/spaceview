package com.jpromi.spaceview.elements

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.jpromi.spaceview.enums.AssetSourceType
import com.jpromi.spaceview.models.Image

@Composable
fun ImageView(
    modifier: Modifier = Modifier,
    image: Image? = null,
    contentScale: ContentScale = ContentScale.Fit,
) {
    if (image != null) {
        when (image.sourceType) {
            AssetSourceType.REMOTE -> {
                AsyncImage(
                    modifier = modifier,
                    model = image.path,
                    contentDescription = image.description,
                    contentScale = contentScale,
                )
            }
            else -> {
                null
            }
        }
    }
}