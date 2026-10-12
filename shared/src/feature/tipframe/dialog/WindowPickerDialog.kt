package com.ma7moud3ly.tipframe.feature.tipframe.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.tipframe.feature.tipframe.canvas.FrameBar
import com.ma7moud3ly.tipframe.model.WindowFrame
import com.ma7moud3ly.tipframe.resources.Res
import com.ma7moud3ly.tipframe.resources.frame_browser
import com.ma7moud3ly.tipframe.resources.frame_mac
import com.ma7moud3ly.tipframe.resources.frame_none
import com.ma7moud3ly.tipframe.resources.frame_phone
import com.ma7moud3ly.tipframe.resources.frame_terminal
import com.ma7moud3ly.tipframe.resources.frame_window
import com.ma7moud3ly.tipframe.resources.frame_windows
import com.ma7moud3ly.tipframe.ui.components.MyDialog
import com.ma7moud3ly.tipframe.ui.theme.AppTheme
import com.ma7moud3ly.tipframe.ui.theme.AppThemes
import io.ma7moud3ly.nemo.model.EditorTheme
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun WindowGridPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            WindowGrid(
                selected = WindowFrame.MAC,
                theme = AppThemes.DEFAULT_DARK,
                onSelect = {}
            )
        }
    }
}

@Preview
@Composable
private fun WindowGridPreviewLight() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            WindowGrid(
                selected = WindowFrame.BROWSER,
                theme = AppThemes.DEFAULT_LIGHT,
                onSelect = {}
            )
        }
    }
}

/**
 * Dialog that shows every window style in a grid. Picking one applies it to
 * the code frame and closes the dialog.
 *
 * @param theme the code theme the miniatures are drawn in
 */
@Composable
internal fun WindowPickerDialog(
    selected: WindowFrame,
    theme: EditorTheme,
    onSelect: (WindowFrame) -> Unit,
    onDismiss: () -> Unit
) {
    MyDialog(
        title = stringResource(Res.string.frame_window),
        onDismiss = onDismiss
    ) {
        WindowGrid(
            selected = selected,
            theme = theme,
            onSelect = {
                onSelect(it)
                onDismiss()
            },
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

@Composable
private fun WindowGrid(
    selected: WindowFrame,
    theme: EditorTheme,
    onSelect: (WindowFrame) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(WindowFrame.entries, key = { it.key }) { frame ->
            WindowCard(
                frame = frame,
                theme = theme,
                isSelected = frame == selected,
                onClick = { onSelect(frame) }
            )
        }
    }
}

/** A window style as a card: a miniature of the window's top, over its name. */
@Composable
private fun WindowCard(
    frame: WindowFrame,
    theme: EditorTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, shape)
    ) {
        Column {
            // the window's own title bar, on a strip of the window's body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Color(theme.background))
            ) {
                FrameBar(
                    frame = frame,
                    title = "",
                    theme = theme,
                    onTitleChange = {},
                    showHint = false
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        when (frame) {
                            WindowFrame.MAC -> Res.string.frame_mac
                            WindowFrame.WINDOWS -> Res.string.frame_windows
                            WindowFrame.TERMINAL -> Res.string.frame_terminal
                            WindowFrame.BROWSER -> Res.string.frame_browser
                            WindowFrame.PHONE -> Res.string.frame_phone
                            WindowFrame.NONE -> Res.string.frame_none
                        }
                    ),
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
        // covers the miniature, so a click picks the style instead of editing its title
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = onClick)
        )
    }
}
