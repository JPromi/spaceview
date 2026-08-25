package com.jpromi.spaceview.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.jpromi.spaceview.AppTheme
import com.jpromi.spaceview.elements.forms.SettingsButton
import com.mikepenz.aboutlibraries.Libs
import kotlinx.coroutines.launch
import spaceview.shared.generated.resources.Res

@Composable
fun LibrariesView() {
    val coroutineScope = rememberCoroutineScope()
    var showLicence by remember { mutableStateOf(false) }
    var libs by remember { mutableStateOf<Libs?>(null) }

    SettingsButton(
        text = "Show Licenses",
        onClick = {
            coroutineScope.launch {
                libs = Libs.Builder()
                    .withJson(
                        Res.readBytes("files/aboutlibraries.json").decodeToString()
                    )
                    .build()
                showLicence = true
            }
        }
    )
    
    if (showLicence && libs != null) {
        Spacer(Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            for (library in libs!!.libraries) {
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