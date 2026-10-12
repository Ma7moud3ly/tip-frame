package com.ma7moud3ly.tipframe.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import io.ma7moud3ly.nemo.model.EditorTheme

/** How long the page takes to fade from one theme to the next, in milliseconds. */
private const val THEME_FADE_MS = 350

internal fun Long.toColor() = Color(this)

/** The theme's accent: the color of its active line number. */
val EditorTheme.accent: Color get() = lineNumberActive.toColor()

/**
 * Maps a Nemo [EditorTheme] onto a Material [ColorScheme], so the page is
 * painted with the palette of an editor theme.
 *
 * Roles are taken from the editor's own parts:
 * - `background` <- editor background, `surface` <- gutter (bars and panels)
 * - `surfaceVariant` and the high containers <- current-line highlight (selected fills)
 * - `outline` / `outlineVariant` <- selection (borders and hairlines)
 * - `primary` <- the accent (buttons, sliders, switches, selected marks)
 * - `secondary` / `tertiary` <- keyword and string colors
 * - `error` <- type color
 */
fun EditorTheme.toAppColorScheme(): ColorScheme {
    val background = background.toColor()
    val foreground = foreground.toColor()
    val gutter = gutter.toColor()
    val currentLine = currentLineBackground.toColor()
    val selection = selection.toColor()
    val lineNumber = lineNumber.toColor()
    val accent = accent

    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = accent,
        onPrimary = background,
        primaryContainer = lerp(background, accent, 0.25f),
        onPrimaryContainer = foreground,
        inversePrimary = accent,
        secondary = syntax.keyword.toColor(),
        onSecondary = background,
        secondaryContainer = currentLine,
        onSecondaryContainer = foreground,
        tertiary = syntax.string.toColor(),
        onTertiary = background,
        tertiaryContainer = currentLine,
        onTertiaryContainer = foreground,
        background = background,
        onBackground = foreground,
        surface = gutter,
        onSurface = foreground,
        surfaceVariant = currentLine,
        onSurfaceVariant = lineNumber,
        surfaceTint = accent,
        inverseSurface = foreground,
        inverseOnSurface = background,
        error = syntax.type.toColor(),
        onError = background,
        outline = selection,
        outlineVariant = lerp(gutter, selection, 0.5f),
        surfaceBright = currentLine,
        surfaceDim = background,
        surfaceContainerLowest = background,
        surfaceContainerLow = gutter,
        surfaceContainer = gutter,
        surfaceContainerHigh = currentLine,
        surfaceContainerHighest = currentLine
    )
}

/** This scheme with its main colors fading to their new values when the theme changes. */
@Composable
private fun ColorScheme.animated(): ColorScheme {
    @Composable
    fun Color.fade(): Color {
        val color by animateColorAsState(this, tween(THEME_FADE_MS))
        return color
    }
    return copy(
        primary = primary.fade(),
        onPrimary = onPrimary.fade(),
        primaryContainer = primaryContainer.fade(),
        secondary = secondary.fade(),
        secondaryContainer = secondaryContainer.fade(),
        tertiary = tertiary.fade(),
        background = background.fade(),
        onBackground = onBackground.fade(),
        surface = surface.fade(),
        onSurface = onSurface.fade(),
        surfaceVariant = surfaceVariant.fade(),
        onSurfaceVariant = onSurfaceVariant.fade(),
        outline = outline.fade(),
        outlineVariant = outlineVariant.fade(),
        surfaceContainer = surfaceContainer.fade(),
        surfaceContainerHigh = surfaceContainerHigh.fade(),
        surfaceContainerHighest = surfaceContainerHighest.fade()
    )
}

/** The theme the page is painted with: one of the two app themes. */
val LocalEditorTheme = staticCompositionLocalOf { AppThemes.DEFAULT_FRAME_THEME }

@Composable
fun AppTheme(
    theme: EditorTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = remember(theme) { theme.toAppColorScheme() }
    CompositionLocalProvider(LocalEditorTheme provides theme) {
        MaterialTheme(
            colorScheme = colorScheme.animated(),
            typography = appTypography(),
            content = content
        )
    }
}

/** Paints the page with the app's dark theme or its light theme. */
@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) = AppTheme(
    theme = if (darkTheme) AppThemes.DEFAULT_DARK else AppThemes.DEFAULT_LIGHT,
    content = content
)
