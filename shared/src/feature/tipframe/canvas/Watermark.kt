package com.ma7moud3ly.tipframe.feature.tipframe.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.tipframe.resources.Res
import com.ma7moud3ly.tipframe.resources.app_name
import com.ma7moud3ly.tipframe.ui.theme.AppThemes
import com.ma7moud3ly.tipframe.ui.theme.watermarkFontFamily
import io.ma7moud3ly.nemo.model.EditorTheme
import org.jetbrains.compose.resources.stringResource

/** How see-through the watermark is: 1 is solid, 0 is invisible. */
private const val WATERMARK_ALPHA = 1f

@Preview
@Composable
private fun WatermarkPreviewDark() {
    WatermarkPreview(AppThemes.DEFAULT_DARK)
}

@Preview
@Composable
private fun WatermarkPreviewLight() {
    WatermarkPreview(AppThemes.DEFAULT_LIGHT)
}

@Composable
private fun WatermarkPreview(theme: EditorTheme) {
    Box(
        modifier = Modifier
            .background(Color(theme.background))
            .padding(16.dp)
    ) {
        Watermark(theme)
    }
}

/**
 * The app's mark, drawn over the code: the app's name in large outlined
 * letters of the theme's text color, mostly see-through, so the code under
 * them stays readable. The outline comes from the font, see [watermarkFontFamily].
 */
@Composable
internal fun Watermark(
    theme: EditorTheme,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(Res.string.app_name),
        fontFamily = watermarkFontFamily(),
        fontWeight = FontWeight.Thin,
        fontSize = 16.sp,
        color = Color(theme.syntax.keyword).copy(alpha = WATERMARK_ALPHA),
        maxLines = 1,
        modifier = modifier
    )
}
