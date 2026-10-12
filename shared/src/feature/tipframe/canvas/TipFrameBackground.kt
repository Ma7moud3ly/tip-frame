package com.ma7moud3ly.tipframe.feature.tipframe.canvas

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.tipframe.model.BackgroundStyle
import com.ma7moud3ly.tipframe.model.TipFrameConfig
import io.ma7moud3ly.nemo.model.EditorTheme

/** Distance between the marks of a pattern. */
private val PatternStep = 22.dp

/** Color of the marks of a pattern. */
private val PatternColor = Color.White.copy(alpha = 0.22f)

/** Where the blobs of a mesh sit, as fractions of the width and the height. */
private val MeshPoints = listOf(
    Offset(0.10f, 0.15f),
    Offset(0.90f, 0.10f),
    Offset(0.80f, 0.95f),
    Offset(0.15f, 0.90f)
)

/**
 * The background palettes of this theme: [TipFrameConfig.PALETTE_COUNT] lists of
 * three colors, each taken from the theme's syntax colors.
 */
internal fun EditorTheme.palettes(): List<List<Color>> = listOf(
    listOf(syntax.keyword, syntax.function, syntax.type),
    listOf(syntax.string, syntax.function, syntax.keyword),
    listOf(syntax.type, syntax.number, syntax.keyword),
    listOf(syntax.function, syntax.variable, syntax.string),
    listOf(syntax.number, syntax.string, syntax.type),
    listOf(lineNumberActive, syntax.keyword, syntax.string)
).map { colors -> colors.map { Color(it) } }

/** The palette at [index]; an index out of range gives the nearest one. */
internal fun EditorTheme.palette(index: Int): List<Color> {
    val palettes = palettes()
    return palettes[index.coerceIn(0, palettes.lastIndex)]
}

/**
 * Paints the background of an image behind this node.
 *
 * @param style what to paint
 * @param colors the palette to paint with, see [palette]
 * @param base the window's own color: all that [BackgroundStyle.NONE] paints,
 *   and the color under the blobs of a [BackgroundStyle.MESH]
 */
internal fun Modifier.tipFrameBackground(
    style: BackgroundStyle,
    colors: List<Color>,
    base: Color
): Modifier = drawBehind {
    when (style) {
        BackgroundStyle.NONE -> drawRect(base)
        BackgroundStyle.SOLID -> drawRect(colors.first())
        BackgroundStyle.GRADIENT -> drawGradient(colors)
        BackgroundStyle.MESH -> drawMesh(colors, base)
        BackgroundStyle.DOTS -> {
            drawGradient(colors)
            drawDots()
        }

        BackgroundStyle.GRID -> {
            drawGradient(colors)
            drawGrid()
        }

        BackgroundStyle.LINES -> {
            drawGradient(colors)
            drawDiagonalLines()
        }
    }
}

/** A gradient of the first two [colors], from the top left to the bottom right. */
private fun DrawScope.drawGradient(colors: List<Color>) {
    drawRect(
        brush = Brush.linearGradient(
            colors = colors.take(2),
            start = Offset.Zero,
            end = Offset(size.width, size.height)
        )
    )
}

/** [base] with a soft blob of each color fading out from the points in [MeshPoints]. */
private fun DrawScope.drawMesh(colors: List<Color>, base: Color) {
    drawRect(base)
    val radius = maxOf(size.width, size.height) * 0.75f
    MeshPoints.forEachIndexed { index, point ->
        val color = colors[index % colors.size]
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.9f), color.copy(alpha = 0f)),
                center = Offset(size.width * point.x, size.height * point.y),
                radius = radius
            )
        )
    }
}

/** A dot every [PatternStep] in both directions. */
private fun DrawScope.drawDots() {
    val step = PatternStep.toPx()
    val radius = 1.5.dp.toPx()
    var y = step / 2
    while (y < size.height) {
        var x = step / 2
        while (x < size.width) {
            drawCircle(color = PatternColor, radius = radius, center = Offset(x, y))
            x += step
        }
        y += step
    }
}

/** A vertical and a horizontal line every [PatternStep]. */
private fun DrawScope.drawGrid() {
    val step = PatternStep.toPx()
    val stroke = 1.dp.toPx()
    var x = step
    while (x < size.width) {
        drawLine(PatternColor, Offset(x, 0f), Offset(x, size.height), stroke)
        x += step
    }
    var y = step
    while (y < size.height) {
        drawLine(PatternColor, Offset(0f, y), Offset(size.width, y), stroke)
        y += step
    }
}

/** A line rising to the right every [PatternStep]. */
private fun DrawScope.drawDiagonalLines() {
    val step = PatternStep.toPx()
    val stroke = 1.dp.toPx()
    var x = -size.height
    while (x < size.width) {
        drawLine(PatternColor, Offset(x, size.height), Offset(x + size.height, 0f), stroke)
        x += step
    }
}
