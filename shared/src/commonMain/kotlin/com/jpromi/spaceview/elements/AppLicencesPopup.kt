package com.jpromi.spaceview.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.jpromi.spaceview.AppTheme
import com.mikepenz.aboutlibraries.Libs
import spaceview.shared.generated.resources.Res

@Composable
fun AppLicencesPopup(
    state: PopupState
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
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x75000000))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = state::close
                ),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                for (library in libs?.libraries.orEmpty()) {
                    Expandable(title = library.name)
                    {
                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            if (library.website != null) {
                                Text(buildAnnotatedString {
                                    append("Website: ")
                                    withLink(
                                        LinkAnnotation.Url(
                                            library.website!!,
                                            TextLinkStyles(style = SpanStyle(color = AppTheme.linkColor))
                                        )
                                    ) {
                                        append(library.website)
                                    }
                                }, color = AppTheme.textColor)
                            }

                            for (licence in library.licenses) {
                                Text("License: ${licence.name}", color = AppTheme.textColor)
                                Spacer(Modifier.height(10.dp))
                                Text(licence.licenseContent ?: "", color = AppTheme.textColor)
                            }
                        }
                    }
                }
            }
        }
    }
}
