package com.ma7moud3ly.tipframe.feature.tipframe.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.tipframe.model.WindowFrame
import com.ma7moud3ly.tipframe.resources.Res
import com.ma7moud3ly.tipframe.resources.frame_title_hint
import com.ma7moud3ly.tipframe.ui.theme.AppThemes
import com.ma7moud3ly.tipframe.ui.theme.accent
import com.ma7moud3ly.tipframe.ui.theme.codeFontFamily
import com.ma7moud3ly.tipframe.ui.theme.uiFontFamily
import io.ma7moud3ly.nemo.model.EditorTheme
import org.jetbrains.compose.resources.stringResource

/** Height of a window's title bar. */
private val BarHeight = 40.dp

@Preview
@Composable
private fun FrameBarsPreviewDark() {
    FrameBarsPreview(AppThemes.DEFAULT_DARK)
}

@Preview
@Composable
private fun FrameBarsPreviewLight() {
    FrameBarsPreview(AppThemes.DEFAULT_LIGHT)
}

@Composable
private fun FrameBarsPreview(theme: EditorTheme) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        WindowFrame.entries.forEach { frame ->
            FrameBar(
                frame = frame,
                title = "Greeting.kt",
                theme = theme,
                onTitleChange = {}
            )
        }
    }
}

/**
 * The title bar of a window, drawn in the style of [frame].
 *
 * The title is edited in place, except on [WindowFrame.PHONE], which shows a
 * notch instead, and [WindowFrame.NONE], which has no bar.
 *
 * @param showHint whether an empty title shows the "Untitled" hint
 */
@Composable
internal fun FrameBar(
    frame: WindowFrame,
    title: String,
    theme: EditorTheme,
    onTitleChange: (String) -> Unit,
    showHint: Boolean = true
) {
    when (frame) {
        WindowFrame.MAC -> MacBar(title, theme, onTitleChange, showHint)
        WindowFrame.WINDOWS -> WindowsBar(title, theme, onTitleChange, showHint)
        WindowFrame.TERMINAL -> TerminalBar(title, theme, onTitleChange, showHint)
        WindowFrame.BROWSER -> BrowserBar(title, theme, onTitleChange, showHint)
        WindowFrame.PHONE -> PhoneNotch(theme)
        WindowFrame.NONE -> Unit
    }
}

/** Three colored buttons on the left and the title in the middle. */
@Composable
private fun MacBar(
    title: String,
    theme: EditorTheme,
    onTitleChange: (String) -> Unit,
    showHint: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(BarHeight)
            .background(Color(theme.gutter))
            .padding(horizontal = 14.dp)
    ) {
        TrafficLights(Modifier.align(Alignment.CenterStart))
        TitleField(
            title = title,
            theme = theme,
            onTitleChange = onTitleChange,
            showHint = showHint,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.6f)
        )
    }
}

/** The title on the left and the minimize, maximize and close glyphs on the right. */
@Composable
private fun WindowsBar(
    title: String,
    theme: EditorTheme,
    onTitleChange: (String) -> Unit,
    showHint: Boolean
) {
    val glyphColor = Color(theme.lineNumber)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(BarHeight)
            .background(Color(theme.gutter))
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleField(
            title = title,
            theme = theme,
            onTitleChange = onTitleChange,
            showHint = showHint,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
        WindowGlyph { line(glyphColor, Offset(0f, size.height / 2), Offset(size.width, size.height / 2)) }
        WindowGlyph { drawRect(color = glyphColor, style = Stroke(width = 1.2.dp.toPx())) }
        WindowGlyph {
            line(glyphColor, Offset.Zero, Offset(size.width, size.height))
            line(glyphColor, Offset(0f, size.height), Offset(size.width, 0f))
        }
    }
}

/** A prompt mark followed by the title. */
@Composable
private fun TerminalBar(
    title: String,
    theme: EditorTheme,
    onTitleChange: (String) -> Unit,
    showHint: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(BarHeight)
            .background(Color(theme.gutter))
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ">_",
            fontFamily = codeFontFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = theme.accent
        )
        TitleField(
            title = title,
            theme = theme,
            onTitleChange = onTitleChange,
            showHint = showHint,
            textAlign = TextAlign.Start,
            fontFamily = codeFontFamily(),
            modifier = Modifier.weight(1f)
        )
    }
}

/** Three colored buttons, then the title inside an address field. */
@Composable
private fun BrowserBar(
    title: String,
    theme: EditorTheme,
    onTitleChange: (String) -> Unit,
    showHint: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(BarHeight)
            .background(Color(theme.gutter))
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TrafficLights()
        Box(
            modifier = Modifier
                .weight(1f)
                .height(26.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(theme.currentLineBackground))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            TitleField(
                title = title,
                theme = theme,
                onTitleChange = onTitleChange,
                showHint = showHint,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** The pill at the top of a phone's screen. */
@Composable
private fun PhoneNotch(theme: EditorTheme) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 76.dp, height = 18.dp)
                .clip(CircleShape)
                .background(phoneBodyColor(theme.dark))
        )
    }
}

/** The bar at the bottom of a phone's screen. */
@Composable
internal fun PhoneHomeBar(theme: EditorTheme) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 104.dp, height = 4.dp)
                .clip(CircleShape)
                .background(Color(theme.lineNumber))
        )
    }
}

/** The color of a phone's body: black on a dark theme, near black on a light one. */
internal fun phoneBodyColor(dark: Boolean): Color =
    if (dark) Color(0xFF05070A) else Color(0xFF1B1D22)

/** The close, minimize and maximize buttons of a macOS window. */
@Composable
private fun TrafficLights(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(0xFFFF5F57, 0xFFFEBC2E, 0xFF28C840).forEach { color ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(color))
            )
        }
    }
}

/** A 10dp glyph of a Windows title bar, drawn by [draw]. */
@Composable
private fun WindowGlyph(draw: DrawScope.() -> Unit) {
    Canvas(modifier = Modifier.size(10.dp), onDraw = draw)
}

private fun DrawScope.line(color: Color, start: Offset, end: Offset) {
    drawLine(color = color, start = start, end = end, strokeWidth = 1.2.dp.toPx())
}

/** The window title as a one-line field. While it is empty it shows a hint, unless [showHint] is off. */
@Composable
private fun TitleField(
    title: String,
    theme: EditorTheme,
    onTitleChange: (String) -> Unit,
    showHint: Boolean,
    textAlign: TextAlign,
    modifier: Modifier = Modifier,
    fontFamily: FontFamily = uiFontFamily()
) {
    val style = TextStyle(
        color = Color(theme.foreground).copy(alpha = 0.85f),
        fontSize = 13.sp,
        fontFamily = fontFamily,
        textAlign = textAlign
    )
    BasicTextField(
        value = title,
        onValueChange = onTitleChange,
        singleLine = true,
        textStyle = style,
        cursorBrush = SolidColor(theme.accent),
        modifier = modifier,
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart,
                propagateMinConstraints = true
            ) {
                if (showHint && title.isEmpty()) Text(
                    text = stringResource(Res.string.frame_title_hint),
                    style = style.copy(color = style.color.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                )
                innerTextField()
            }
        }
    )
}
