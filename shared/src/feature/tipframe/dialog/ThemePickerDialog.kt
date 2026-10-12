package com.ma7moud3ly.tipframe.feature.tipframe.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ma7moud3ly.tipframe.resources.Res
import com.ma7moud3ly.tipframe.resources.tools_theme
import com.ma7moud3ly.tipframe.ui.components.MyDialog
import com.ma7moud3ly.tipframe.ui.theme.AppTheme
import com.ma7moud3ly.tipframe.ui.theme.AppThemes
import io.ma7moud3ly.nemo.model.EditorTheme
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun ThemeGridPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            ThemeGrid(selected = AppThemes.DEFAULT_DARK, onSelect = {})
        }
    }
}

@Preview
@Composable
private fun ThemeGridPreviewLight() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            ThemeGrid(selected = AppThemes.DEFAULT_LIGHT, onSelect = {})
        }
    }
}

/**
 * Dialog that shows every code theme in a grid. Picking one applies it to the
 * code frame and closes the dialog.
 */
@Composable
internal fun ThemePickerDialog(
    selected: EditorTheme,
    onSelect: (EditorTheme) -> Unit,
    onDismiss: () -> Unit
) {
    MyDialog(
        title = stringResource(Res.string.tools_theme),
        onDismiss = onDismiss,
        maxWidth = 640.dp
    ) {
        ThemeGrid(
            selected = selected,
            onSelect = {
                onSelect(it)
                onDismiss()
            },
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

@Composable
private fun ThemeGrid(
    selected: EditorTheme,
    onSelect: (EditorTheme) -> Unit,
    modifier: Modifier = Modifier,
    themes: List<EditorTheme> = AppThemes.ALL
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 132.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(themes, key = { it.name }) { theme ->
            ThemeCard(
                theme = theme,
                isSelected = theme.name == selected.name,
                onClick = { onSelect(theme) }
            )
        }
    }
}

/** A theme as a card: a miniature of code in its colors, over its name. */
@Composable
private fun ThemeCard(
    theme: EditorTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, shape)
            .clickable(onClick = onClick)
    ) {
        MiniCode(theme)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = theme.name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) Text(
                text = "✓",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** Four lines of bars in the theme's syntax colors, on the theme's background. */
@Composable
private fun MiniCode(theme: EditorTheme) {
    val syntax = theme.syntax
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(theme.background))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CodeLine(0.dp, 22.dp to syntax.keyword, 34.dp to syntax.function)
        CodeLine(10.dp, 18.dp to syntax.variable, 6.dp to syntax.operator, 38.dp to syntax.string)
        CodeLine(10.dp, 26.dp to syntax.type, 12.dp to syntax.number)
        CodeLine(0.dp, 48.dp to syntax.comment)
    }
}

/** One line of the miniature: bars of the given widths and colors after an [indent]. */
@Composable
private fun CodeLine(indent: Dp, vararg bars: Pair<Dp, Long>) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (indent > 0.dp) Spacer(Modifier.width(indent))
        bars.forEach { (width, color) ->
            Box(
                modifier = Modifier
                    .width(width)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(Color(color))
            )
        }
    }
}
