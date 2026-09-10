package com.jpromi.spaceview.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil3.compose.AsyncImage
import com.jpromi.spaceview.AppTheme
import com.jpromi.spaceview.AppSettings
import com.jpromi.spaceview.elements.forms.ScrollColumn
import com.jpromi.spaceview.enums.AssetSourceType
import com.jpromi.spaceview.models.Image
import com.mikepenz.aboutlibraries.Libs
import spaceview.shared.generated.resources.Res
import kotlin.collections.orEmpty

@Composable
fun SettingsImageSelectPopup(
    state: PopupState,
    title: String? = null,
    images: List<Image> = emptyList(),
    onSelect: ((image: Image?) -> Unit)? = null,

    // ToDo: needs to be implemented
    allowSelfUpload: Boolean = false,
    allowUrlEntry: Boolean = false,
) {
    if (!state.isVisible) return

    var libs by remember { mutableStateOf<Libs?>(null) }

    LaunchedEffect(true) {
        libs = Libs.Builder()
            .withJson(
                Res.readBytes("files/aboutlibraries.json").decodeToString()
            )
            .build()
    }

    Popup(
        onDismissRequest = state::close,
        alignment = Alignment.Center,
        properties = fullScreenPopupProperties(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x75000000))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = state::close
                )
                .windowInsetsPadding(WindowInsets.safeDrawing),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .background(
                        color = AppTheme.background,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .fillMaxWidth(0.8f)
                    .border(1.dp, AppTheme.borderSettings, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = TextStyle()
                    )
                }

                ScrollColumn(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    item {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            for (image: Image in images.orEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .height(189.dp)
                                        .width(250.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(1.dp, AppTheme.borderSettings, RoundedCornerShape(16.dp))
                                        .clickable {
                                            onSelect?.invoke(image)
                                        }
                                ) {
                                    when (image.sourceType) {
                                        AssetSourceType.REMOTE -> {
                                            AsyncImage(
                                                model = image.path,
                                                contentDescription = image.description,

                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxWidth()
                                            )
                                        }
                                        else -> {

                                        }
                                    }

                                    if (image.description != null) {
                                        HorizontalDivider(
                                            color = AppTheme.borderSettings,
                                        )

                                        Text(
                                            text = image.description,
                                            color = AppTheme.textColor,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}
