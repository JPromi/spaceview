package com.jpromi.spaceview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import spaceview.shared.generated.resources.Montserrat_Italic_VariableFont_wght
import spaceview.shared.generated.resources.Montserrat_VariableFont_wght
import spaceview.shared.generated.resources.Res

object AppTheme {
    val background = Color(0xff05152C)
    val primary = Color(0xFF11BD65)
    val textColor = Color(0xffffffff)
    val textColorGreen = Color(0xff4caf50)
    val textColorRed = Color(0xfff44336)
    val slotBackground = Color(0x20f5f5f5)

    val freeTagBackground = Color(0xff4caf50)
    val freeTabTextColor = Color(0xfff5f5f5)

    val busyTagBackground = Color(0xfff44336)
    val busyTabTextColor = Color(0xfff5f5f5)

    val backgroundSettings = Color(0x15f5f5f5)
    val borderSettings = Color(0x30f5f5f5)
    val linkColor = Color(0xff778FDC)

    private val colorScheme = darkColorScheme(
        primary = primary,
        background = background,
        surface = background,
        onPrimary = textColor,
        onBackground = textColor,
        onSurface = textColor,
        error = busyTagBackground,
        onError = busyTabTextColor,
    )

    @Composable
    operator fun invoke(content: @Composable () -> Unit) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(fontFamily = montserratFontFamily()),
            content = content,
        )
    }

    @Composable
    private fun montserratFontFamily(): FontFamily {
        return FontFamily(
            Font(
                resource = Res.font.Montserrat_VariableFont_wght,
                weight = FontWeight.Normal,
            ),
            Font(
                resource = Res.font.Montserrat_VariableFont_wght,
                weight = FontWeight.Medium,
            ),
            Font(
                resource = Res.font.Montserrat_VariableFont_wght,
                weight = FontWeight.SemiBold,
            ),
            Font(
                resource = Res.font.Montserrat_VariableFont_wght,
                weight = FontWeight.Bold,
            ),
            Font(
                resource = Res.font.Montserrat_Italic_VariableFont_wght,
                weight = FontWeight.Normal,
                style = FontStyle.Italic,
            ),
        )
    }
}
