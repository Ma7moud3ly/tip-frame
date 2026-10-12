package com.ma7moud3ly.tipframe.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.ma7moud3ly.tipframe.resources.Res
import com.ma7moud3ly.tipframe.resources.inter_regular
import com.ma7moud3ly.tipframe.resources.inter_semibold
import com.ma7moud3ly.tipframe.resources.jetbrains_mono_bold
import com.ma7moud3ly.tipframe.resources.jetbrains_mono_italic
import com.ma7moud3ly.tipframe.resources.jetbrains_mono_regular
import com.ma7moud3ly.tipframe.resources.tourney_thin
import org.jetbrains.compose.resources.Font

/**
 * The font of the code: JetBrains Mono.
 *
 * The editor draws code in the regular face, keywords in the bold face and
 * comments in the italic face.
 */
@Composable
fun codeFontFamily(): FontFamily = FontFamily(
    Font(Res.font.jetbrains_mono_regular, FontWeight.Normal, FontStyle.Normal),
    Font(Res.font.jetbrains_mono_italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.jetbrains_mono_bold, FontWeight.Bold, FontStyle.Normal)
)

/**
 * The font of every text that is not code: the app's own labels, and the
 * window title and the author badge inside the image. It is Inter, in a
 * regular and a semi-bold face.
 */
@Composable
fun uiFontFamily(): FontFamily = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal, FontStyle.Normal),
    Font(Res.font.inter_semibold, FontWeight.SemiBold, FontStyle.Normal)
)

/**
 * The font of the watermark: Tourney in its extra-light face, whose letters
 * are drawn as thin outlines by the font itself.
 */
@Composable
fun watermarkFontFamily(): FontFamily = FontFamily(
    Font(Res.font.tourney_thin, FontWeight.Thin, FontStyle.Normal)
)

/** Material's text styles, each set in [uiFontFamily]. */
@Composable
fun appTypography(): Typography {
    val font = uiFontFamily()
    return remember(font) {
        val base = Typography()
        base.copy(
            displayLarge = base.displayLarge.copy(fontFamily = font),
            displayMedium = base.displayMedium.copy(fontFamily = font),
            displaySmall = base.displaySmall.copy(fontFamily = font),
            headlineLarge = base.headlineLarge.copy(fontFamily = font),
            headlineMedium = base.headlineMedium.copy(fontFamily = font),
            headlineSmall = base.headlineSmall.copy(fontFamily = font),
            titleLarge = base.titleLarge.copy(fontFamily = font),
            titleMedium = base.titleMedium.copy(fontFamily = font),
            titleSmall = base.titleSmall.copy(fontFamily = font),
            bodyLarge = base.bodyLarge.copy(fontFamily = font),
            bodyMedium = base.bodyMedium.copy(fontFamily = font),
            bodySmall = base.bodySmall.copy(fontFamily = font),
            labelLarge = base.labelLarge.copy(fontFamily = font),
            labelMedium = base.labelMedium.copy(fontFamily = font),
            labelSmall = base.labelSmall.copy(fontFamily = font)
        )
    }
}
