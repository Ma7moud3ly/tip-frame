package com.ma7moud3ly.tipframe.feature.tipframe.canvas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.tipframe.feature.tipframe.EditorPadding
import com.ma7moud3ly.tipframe.feature.tipframe.guardBackticks
import com.ma7moud3ly.tipframe.model.AuthorAvatar
import com.ma7moud3ly.tipframe.model.BackgroundStyle
import com.ma7moud3ly.tipframe.model.EXPORT_SCALE
import com.ma7moud3ly.tipframe.model.TipFrameConfig
import com.ma7moud3ly.tipframe.model.WindowFrame
import com.ma7moud3ly.tipframe.ui.theme.AppTheme
import com.ma7moud3ly.tipframe.ui.theme.AppThemes
import com.ma7moud3ly.tipframe.ui.theme.codeFontFamily
import io.ma7moud3ly.nemo.NemoCodeEditor
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import kotlin.math.pow
import kotlin.math.roundToInt

/** The editor's line height as a multiple of its font size. */
private const val LINE_HEIGHT_RATIO = 1.5f

/** The widest a phone window grows to. */
private val PhoneMaxWidth = 380.dp

/** Thickness of a phone window's body around its screen. */
private val PhoneBezel = 8.dp

/** Height kept free at the bottom of the area for the dock floating there. */
private val DockSpace = 84.dp

@Preview
@Composable
private fun TipFrameCanvasPreviewDark() {
    val config = TipFrameConfig(theme = AppThemes.DEFAULT_DARK)
    AppTheme(theme = config.theme) {
        TipFrameCanvas(
            config = config,
            codeState = remember { CodeState(config.code, config.language) },
            settings = previewSettings(config),
            graphicsLayer = rememberGraphicsLayer(),
            onTitleChange = {},
            onWidthChange = {}
        )
    }
}

@Preview
@Composable
private fun TipFrameCanvasPreviewLightBrowser() {
    val config = TipFrameConfig(
        theme = AppThemes.DEFAULT_LIGHT,
        frame = WindowFrame.BROWSER,
        authorAvatar = AuthorAvatar.INITIALS,
        authorName = "Mahmoud Aly",
        authorHandle = "ma7moud3ly"
    )
    AppTheme(theme = config.theme) {
        TipFrameCanvas(
            config = config,
            codeState = remember { CodeState(config.code, config.language) },
            settings = previewSettings(config),
            graphicsLayer = rememberGraphicsLayer(),
            onTitleChange = {},
            onWidthChange = {}
        )
    }
}

/** Editor settings for a preview: the theme of [config] and the code font. */
@Composable
private fun previewSettings(config: TipFrameConfig): EditorSettings {
    val font = codeFontFamily()
    return remember(font) { EditorSettings(theme = config.theme, fontFamily = font) }
}

/**
 * The area that shows the image.
 *
 * The image is laid out at [EXPORT_SCALE] pixels per dp, the size it is
 * exported at, and scaled down to fit the width of this area. It is as wide
 * as the user set and as tall as its code, and scrolls vertically when it is
 * taller than the area.
 *
 * @param graphicsLayer receives the drawn image; export it with `toImageBitmap()`
 * @param onTitleChange called when the title is edited in the window's title bar
 * @param onWidthChange called with the new width in dp while an edge of the image is dragged
 * @param compact whether the window is narrow, like a phone held upright. The
 *   image is then no wider than the area, so it is shown at full size instead
 *   of scaled down, and the width grips are hidden.
 * @param dock content floating at the bottom center of the area, over the
 *   image's scrolling space; it is not part of the exported image
 */
@Composable
internal fun TipFrameCanvas(
    config: TipFrameConfig,
    codeState: CodeState,
    settings: EditorSettings,
    graphicsLayer: GraphicsLayer,
    onTitleChange: (String) -> Unit,
    onWidthChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    dock: @Composable () -> Unit = {}
) {
    val screenDensity = LocalDensity.current.density
    val tipFrameDensity = remember { Density(density = EXPORT_SCALE, fontScale = 1f) }

    BoxWithConstraints(modifier = modifier) {
        // at least as tall as the area, so a short image sits in its middle
        val minHeight = if (constraints.hasBoundedHeight) maxHeight else 0.dp
        val inset = if (compact) 8.dp else 16.dp
        // the width the image is laid out at: the one the user set, or the area's own on a narrow window
        val width = if (compact && constraints.hasBoundedWidth) {
            minOf(config.width.dp, maxWidth - inset * 2)
        } else config.width.dp
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = minHeight)
                // the bottom space keeps the end of a tall image clear of the dock
                .padding(start = inset, top = inset, end = inset, bottom = DockSpace),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.fitWidth(maxScale = screenDensity / EXPORT_SCALE)) {
                CompositionLocalProvider(LocalDensity provides tipFrameDensity) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (compact.not()) WidthHandle(
                            width = config.width,
                            direction = -1,
                            onWidthChange = onWidthChange
                        )
                        TipFrame(
                            config = config,
                            width = width,
                            codeState = codeState,
                            settings = settings,
                            onTitleChange = onTitleChange,
                            modifier = Modifier.capture(graphicsLayer)
                        )
                        if (compact.not()) WidthHandle(
                            width = config.width,
                            direction = 1,
                            onWidthChange = onWidthChange
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = inset, vertical = 14.dp)
        ) {
            dock()
        }
    }
}

/**
 * A grip beside the left or the right edge of the image. Dragging it away from
 * the middle of the image widens the image, and dragging it toward the middle
 * narrows it. It sits outside the image, so it is not part of the exported image.
 *
 * @param width the image's width in dp
 * @param direction 1 for the right edge, -1 for the left edge
 */
@Composable
private fun WidthHandle(
    width: Int,
    direction: Int,
    onWidthChange: (Int) -> Unit
) {
    val currentWidth by rememberUpdatedState(width)
    val currentOnWidthChange by rememberUpdatedState(onWidthChange)
    Box(
        modifier = Modifier
            .size(width = 20.dp, height = 72.dp)
            .pointerHoverIcon(PointerIcon.Hand)
            .pointerInput(direction) {
                var dragged = 0f
                detectHorizontalDragGestures(
                    onDragStart = { dragged = currentWidth.toFloat() },
                    onHorizontalDrag = { change, amount ->
                        change.consume()
                        // the image stays centered, so an edge moves half of the change in width
                        dragged += direction * amount.toDp().value * 2
                        currentOnWidthChange(
                            dragged.roundToInt().coerceIn(TipFrameConfig.WIDTH_MIN, TipFrameConfig.WIDTH_MAX)
                        )
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.85f))
                .border(1.dp, Color.Black.copy(alpha = 0.25f), CircleShape)
        )
    }
}

/**
 * The image itself, [width] wide: the background, with the window inside its
 * padding. With no background the padding takes the window's own color.
 */
@Composable
private fun TipFrame(
    config: TipFrameConfig,
    width: Dp,
    codeState: CodeState,
    settings: EditorSettings,
    onTitleChange: (String) -> Unit,
    modifier: Modifier
) {
    val theme = config.theme
    val colors = remember(theme, config.palette) { theme.palette(config.palette) }
    Box(
        modifier = modifier
            .width(width)
            .tipFrameBackground(
                style = config.background,
                colors = colors,
                base = Color(theme.background)
            )
            .padding(config.padding.dp),
        contentAlignment = Alignment.Center
    ) {
        TipFrameWindow(
            config = config,
            codeState = codeState,
            settings = settings,
            onTitleChange = onTitleChange
        )
    }
}

/** The window: its title bar, the editor with the watermark over its corner, and the author under the code. */
@Composable
private fun TipFrameWindow(
    config: TipFrameConfig,
    codeState: CodeState,
    settings: EditorSettings,
    onTitleChange: (String) -> Unit
) {
    val theme = config.theme
    val isPhone = config.frame == WindowFrame.PHONE
    // with no padding the window fills the image, so its corners are square
    val fillsImage = config.padding == 0 && isPhone.not()
    val cornerRadius = when {
        isPhone -> 36.dp
        fillsImage -> 0.dp
        else -> 16.dp
    }
    val shape = RoundedCornerShape(cornerRadius)
    // a shadow falls on the background, so there is none without one
    val hasShadow = config.shadow && config.background != BackgroundStyle.NONE
    // a neutral line that stands out from the window: light on a dark theme, dark on a light one
    val edgeColor = if (theme.dark) Color.White.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.18f)
    val border = if (isPhone) BorderStroke(PhoneBezel, phoneBodyColor(theme.dark))
    else BorderStroke(1.dp, edgeColor)

    Column(
        modifier = Modifier
            .then(if (isPhone) Modifier.widthIn(max = PhoneMaxWidth) else Modifier)
            .fillMaxWidth()
            .then(
                if (hasShadow) Modifier.softShadow(
                    cornerRadius = cornerRadius,
                    blur = shadowBlur(config.padding),
                    offsetY = shadowOffset(config.padding)
                ) else Modifier
            )
            .clip(shape)
            .background(Color(theme.background))
            .border(border, shape)
            .then(if (isPhone) Modifier.padding(PhoneBezel) else Modifier)
    ) {
        FrameBar(
            frame = config.frame,
            title = config.title,
            theme = theme,
            onTitleChange = onTitleChange
        )
        key(codeState) {
            codeState.guardBackticks()
            Box {
                NemoCodeEditor(
                    state = codeState,
                    settings = settings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(codeHeight(codeState, settings))
                )
                // lies over the bottom right corner of the code, above the author bar
                if (config.watermark) Watermark(
                    theme = theme,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 8.dp)
                )
            }
        }
        AuthorBadge(
            avatar = config.authorAvatar,
            name = config.authorName,
            handle = config.authorHandle,
            theme = theme
        )
        if (isPhone) PhoneHomeBar(theme)
    }
}

/** How far the window's shadow spreads for a [padding] in dp: 60% of it, at most 36dp. */
private fun shadowBlur(padding: Int): Dp = (padding * 0.6f).coerceAtMost(36f).dp

/** How far the window's shadow is shifted down for a [padding] in dp: 20% of it, at most 12dp. */
private fun shadowOffset(padding: Int): Dp = (padding * 0.2f).coerceAtMost(12f).dp

/**
 * Draws a soft shadow under this node, shifted down by [offsetY]: rounded
 * shapes that grow up to [blur] past the node, each one faint, so they add up
 * to [color] under the node and fade out toward the edge.
 *
 * It is drawn with plain shapes, so the exported image shows it exactly as
 * the screen does.
 *
 * @param cornerRadius corner radius of the node the shadow belongs to
 */
private fun Modifier.softShadow(
    cornerRadius: Dp,
    blur: Dp,
    offsetY: Dp,
    color: Color = Color.Black.copy(alpha = 0.35f)
): Modifier = drawBehind {
    val steps = 14
    // the alpha that, stacked [steps] times, adds up to the color's own alpha
    val stepColor = color.copy(alpha = 1f - (1f - color.alpha).pow(1f / steps))
    val radius = cornerRadius.toPx()
    val shift = offsetY.toPx()
    for (step in 1..steps) {
        val spread = blur.toPx() * step / steps
        drawRoundRect(
            color = stepColor,
            topLeft = Offset(-spread, -spread + shift),
            size = Size(size.width + 2 * spread, size.height + 2 * spread),
            cornerRadius = CornerRadius(radius + spread)
        )
    }
}

/** The height the editor needs to show every line of the code without scrolling. */
@Composable
private fun codeHeight(codeState: CodeState, settings: EditorSettings): Dp {
    val fontSize = settings.fontSizeState.value
    val lineHeight = with(LocalDensity.current) { (fontSize * LINE_HEIGHT_RATIO).sp.toDp() }
    return lineHeight * codeState.totalLines +
            EditorPadding.calculateTopPadding() +
            EditorPadding.calculateBottomPadding() +
            2.dp
}

/**
 * Records what this node draws into [layer], then draws the layer in its
 * place. The layer then holds the node at its own size, whatever scale it is
 * shown at.
 */
private fun Modifier.capture(layer: GraphicsLayer): Modifier = drawWithContent {
    layer.record { this@drawWithContent.drawContent() }
    drawLayer(layer)
}

/**
 * Measures the content at the size it asks for, then scales it down to the
 * available width. The content is never shown larger than [maxScale].
 */
private fun Modifier.fitWidth(maxScale: Float): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(Constraints())
    val fit = if (constraints.hasBoundedWidth && placeable.width > 0) {
        constraints.maxWidth.toFloat() / placeable.width
    } else maxScale
    val scale = minOf(maxScale, fit)

    layout(
        width = (placeable.width * scale).roundToInt(),
        height = (placeable.height * scale).roundToInt()
    ) {
        placeable.placeWithLayer(x = 0, y = 0) {
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(0f, 0f)
        }
    }
}
