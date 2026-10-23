package com.ma7moud3ly.tipframe.feature.tipframe

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.tipframe.feature.tipframe.model.TipFrameEvent
import com.ma7moud3ly.tipframe.platform.LocalPlatform
import com.ma7moud3ly.tipframe.platform.Platform
import com.ma7moud3ly.tipframe.platform.isWeb
import com.ma7moud3ly.tipframe.resources.Res
import com.ma7moud3ly.tipframe.resources.app_nemo_link
import com.ma7moud3ly.tipframe.resources.app_nemo_name
import com.ma7moud3ly.tipframe.resources.app_powered_by
import com.ma7moud3ly.tipframe.resources.app_repo_link
import com.ma7moud3ly.tipframe.resources.app_wordmark_frame
import com.ma7moud3ly.tipframe.resources.app_wordmark_tip
import com.ma7moud3ly.tipframe.resources.toolbar_copy_image
import com.ma7moud3ly.tipframe.resources.toolbar_copy_link
import com.ma7moud3ly.tipframe.resources.toolbar_export
import com.ma7moud3ly.tipframe.resources.toolbar_github
import com.ma7moud3ly.tipframe.resources.toolbar_reset
import com.ma7moud3ly.tipframe.resources.toolbar_theme_dark
import com.ma7moud3ly.tipframe.resources.toolbar_theme_light
import com.ma7moud3ly.tipframe.ui.components.ToolbarButton
import com.ma7moud3ly.tipframe.ui.theme.AppTheme
import com.ma7moud3ly.tipframe.ui.theme.LocalEditorTheme
import com.ma7moud3ly.tipframe.ui.theme.codeFontFamily
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun TipFrameToolbarPreviewDark() {
    AppTheme(darkTheme = true) {
        TipFrameToolbar(showLabels = true, uiEvents = {})
    }
}

@Preview
@Composable
private fun TipFrameToolbarPreviewDarkWeb() {
    CompositionLocalProvider(LocalPlatform provides Platform.Web) {
        AppTheme(darkTheme = true) {
            TipFrameToolbar(showLabels = true, uiEvents = {})
        }
    }
}

@Preview
@Composable
private fun TipFrameToolbarPreviewLightCompact() {
    AppTheme(darkTheme = false) {
        TipFrameToolbar(showLabels = false, uiEvents = {})
    }
}

/**
 * The top bar: the wordmark with a credit to nemo-editor under it, then the link to the project's repository, the
 * dark/light toggle, reset, copy link (on the web only), copy image and export.
 *
 * @param showLabels whether the buttons show their text beside their icon
 */
@Composable
internal fun TipFrameToolbar(
    showLabels: Boolean,
    uiEvents: (TipFrameEvent) -> Unit
) {
    val isDark = LocalEditorTheme.current.dark
    val isWeb = LocalPlatform.current.isWeb
    val uriHandler = LocalUriHandler.current
    val repoLink = stringResource(Res.string.app_repo_link)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .height(56.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppBrand()
        Spacer(Modifier.weight(1f))
        ToolbarButton(
            icon = Icons.Outlined.Code,
            label = stringResource(Res.string.toolbar_github),
            showLabel = showLabels,
            onClick = { uriHandler.openUri(repoLink) }
        )
        ToolbarButton(
            icon = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
            label = stringResource(
                if (isDark) Res.string.toolbar_theme_light else Res.string.toolbar_theme_dark
            ),
            showLabel = false,
            onClick = { uiEvents(TipFrameEvent.ToggleAppTheme) }
        )
        ToolbarButton(
            icon = Icons.Outlined.Refresh,
            label = stringResource(Res.string.toolbar_reset),
            showLabel = false,
            onClick = { uiEvents(TipFrameEvent.Reset) }
        )
        // only the web app has an address to share
        if (isWeb) ToolbarButton(
            icon = Icons.Outlined.Link,
            label = stringResource(Res.string.toolbar_copy_link),
            showLabel = showLabels,
            onClick = { uiEvents(TipFrameEvent.CopyLink) }
        )
        ToolbarButton(
            icon = Icons.Outlined.ContentCopy,
            label = stringResource(Res.string.toolbar_copy_image),
            showLabel = showLabels,
            onClick = { uiEvents(TipFrameEvent.CopyImage) }
        )
        ToolbarButton(
            icon = Icons.Outlined.Download,
            label = stringResource(Res.string.toolbar_export),
            showLabel = showLabels,
            filled = true,
            onClick = { uiEvents(TipFrameEvent.Export) }
        )
    }
}

/** The colors of the app icon, from its warm left edge to its cool right edge. */
private val BrandOrange = Color(0xFFFF8A3D)
private val BrandPink = Color(0xFFF0628A)
private val BrandBlue = Color(0xFF4D8DF7)

/**
 * The app's brand: the `{_}` glyph of the app icon, then the wordmark with
 * the credit to nemo-editor under it.
 */
@Composable
private fun AppBrand() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BrandGlyph()
        Column {
            Wordmark()
            PoweredByNemo()
        }
    }
}

/**
 * The `{_}` of the app icon, in the icon's colors: an orange brace, a blue
 * brace, and between them a pink underscore that blinks like a text cursor.
 */
@Composable
private fun BrandGlyph() {
    val caretAlpha = rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1100
                1f at 549
                0f at 550
                0f at 1099
            }
        )
    )
    Row {
        GlyphPart("{", BrandOrange)
        // the alpha is read while drawing, so the blinking only redraws the underscore
        GlyphPart("_", BrandPink, Modifier.graphicsLayer { alpha = caretAlpha.value })
        GlyphPart("}", BrandBlue)
    }
}

@Composable
private fun GlyphPart(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontFamily = codeFontFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 22.sp,
        color = color,
        maxLines = 1,
        modifier = modifier
    )
}

/** The app's name: "Tip" in the regular face and muted, "Frame" in the semi-bold face. */
@Composable
private fun Wordmark() {
    val tip = stringResource(Res.string.app_wordmark_tip)
    val frame = stringResource(Res.string.app_wordmark_frame)
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = muted)) { append(tip) }
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(frame) }
        },
        fontSize = 17.sp,
        lineHeight = 19.sp,
        letterSpacing = (-0.2).sp,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1
    )
}

/**
 * Credit and link to the editor library the code frame is built with, in
 * small print under the wordmark. The library's name takes the brand's blue
 * and is underlined while the pointer is over it.
 */
@Composable
private fun PoweredByNemo() {
    val uriHandler = LocalUriHandler.current
    val link = stringResource(Res.string.app_nemo_link)
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    Row(
        modifier = Modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { uriHandler.openUri(link) }
            ),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.app_powered_by),
            fontSize = 10.sp,
            lineHeight = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Text(
            text = stringResource(Res.string.app_nemo_name),
            fontFamily = codeFontFamily(),
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BrandBlue,
            textDecoration = if (hovered) TextDecoration.Underline else null,
            maxLines = 1
        )
    }
}
