package com.ma7moud3ly.tipframe.feature.tipframe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material.icons.outlined.WebAsset
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.tipframe.feature.tipframe.dialog.AuthorDialog
import com.ma7moud3ly.tipframe.feature.tipframe.dialog.BackgroundDialog
import com.ma7moud3ly.tipframe.feature.tipframe.dialog.ThemePickerDialog
import com.ma7moud3ly.tipframe.feature.tipframe.dialog.WindowPickerDialog
import com.ma7moud3ly.tipframe.feature.tipframe.model.TipFrameEvent
import com.ma7moud3ly.tipframe.model.TipFrameConfig
import com.ma7moud3ly.tipframe.model.displayName
import com.ma7moud3ly.tipframe.model.tipFrameLanguages
import com.ma7moud3ly.tipframe.resources.Res
import com.ma7moud3ly.tipframe.resources.editor_language
import com.ma7moud3ly.tipframe.resources.editor_lines
import com.ma7moud3ly.tipframe.resources.editor_zoom_in
import com.ma7moud3ly.tipframe.resources.editor_zoom_out
import com.ma7moud3ly.tipframe.resources.frame_window
import com.ma7moud3ly.tipframe.resources.tools_author
import com.ma7moud3ly.tipframe.resources.tools_background
import com.ma7moud3ly.tipframe.resources.tools_theme
import com.ma7moud3ly.tipframe.ui.components.ToolChip
import com.ma7moud3ly.tipframe.ui.theme.AppTheme
import io.ma7moud3ly.nemo.model.EditorTheme
import io.ma7moud3ly.nemo.model.Language
import org.jetbrains.compose.resources.stringResource

/** The dialogs the dock opens. */
private enum class DockDialog { THEME, WINDOW, BACKGROUND, AUTHOR }

@Preview
@Composable
private fun TipFrameEditorBarPreviewDark() {
    AppTheme(darkTheme = true) {
        TipFrameEditorBar(
            config = TipFrameConfig(),
            fontSize = 14,
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun TipFrameEditorBarPreviewLight() {
    AppTheme(darkTheme = false) {
        TipFrameEditorBar(
            config = TipFrameConfig(lineNumbers = false),
            fontSize = 14,
            uiEvents = {}
        )
    }
}

@Preview(widthDp = 360)
@Composable
private fun TipFrameEditorBarPreviewDarkCompact() {
    AppTheme(darkTheme = true) {
        TipFrameEditorBar(
            config = TipFrameConfig(),
            fontSize = 14,
            compact = true,
            uiEvents = {}
        )
    }
}

/**
 * The dock floating at the bottom center of the image's area, like the Dock of
 * a macOS desktop. It holds every option of the image: the code's theme and
 * language, the window, the background, the author, the line numbers and the
 * font size. The theme, the window, the background and the author each open a
 * dialog. The dock is as wide as its buttons and scrolls sideways when the
 * window is narrower than that.
 *
 * @param fontSize the editor's font size
 * @param compact whether the window is narrow; the buttons then show icons
 *   instead of text, with less space between them
 */
@Composable
internal fun TipFrameEditorBar(
    config: TipFrameConfig,
    fontSize: Int,
    uiEvents: (TipFrameEvent) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    var dialog by remember { mutableStateOf<DockDialog?>(null) }
    when (dialog) {
        DockDialog.THEME -> ThemePickerDialog(
            selected = config.theme,
            onSelect = { uiEvents(TipFrameEvent.SelectTheme(it)) },
            onDismiss = { dialog = null }
        )

        DockDialog.WINDOW -> WindowPickerDialog(
            selected = config.frame,
            theme = config.theme,
            onSelect = { uiEvents(TipFrameEvent.SelectFrame(it)) },
            onDismiss = { dialog = null }
        )

        DockDialog.BACKGROUND -> BackgroundDialog(
            config = config,
            uiEvents = uiEvents,
            onDismiss = { dialog = null }
        )

        DockDialog.AUTHOR -> AuthorDialog(
            config = config,
            uiEvents = uiEvents,
            onDismiss = { dialog = null }
        )

        null -> Unit
    }

    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .shadow(elevation = 16.dp, shape = shape)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = if (compact) 6.dp else 12.dp, vertical = if (compact) 6.dp else 8.dp),
        horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (compact) BarIconButton(
            icon = Icons.Outlined.Palette,
            label = stringResource(Res.string.tools_theme),
            onClick = { dialog = DockDialog.THEME }
        ) else ThemeButton(
            theme = config.theme,
            onClick = { dialog = DockDialog.THEME }
        )
        LanguageMenu(
            language = config.language,
            compact = compact,
            onSelect = { uiEvents(TipFrameEvent.SelectLanguage(it)) }
        )
        if (compact.not()) BarDivider()
        DialogButton(
            icon = Icons.Outlined.WebAsset,
            text = stringResource(Res.string.frame_window),
            compact = compact,
            onClick = { dialog = DockDialog.WINDOW }
        )
        DialogButton(
            icon = Icons.Outlined.Wallpaper,
            text = stringResource(Res.string.tools_background),
            compact = compact,
            onClick = { dialog = DockDialog.BACKGROUND }
        )
        DialogButton(
            icon = Icons.Outlined.Person,
            text = stringResource(Res.string.tools_author),
            compact = compact,
            onClick = { dialog = DockDialog.AUTHOR }
        )
        if (compact.not()) BarDivider()
        if (compact) BarIconButton(
            icon = Icons.Outlined.FormatListNumbered,
            label = stringResource(Res.string.editor_lines),
            selected = config.lineNumbers,
            onClick = { uiEvents(TipFrameEvent.ToggleLines) }
        ) else ToolChip(
            text = stringResource(Res.string.editor_lines),
            selected = config.lineNumbers,
            onClick = { uiEvents(TipFrameEvent.ToggleLines) }
        )
        if (compact.not()) BarDivider()
        FontSizeStepper(
            fontSize = fontSize,
            compact = compact,
            uiEvents = uiEvents
        )
    }
}

/** A button showing the code theme's colors and name; it opens the theme dialog. */
@Composable
private fun ThemeButton(
    theme: EditorTheme,
    onClick: () -> Unit
) {
    BarButton(onClick = onClick) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(theme.background))
                .padding(horizontal = 5.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Dot(Color(theme.syntax.keyword))
            Dot(Color(theme.syntax.string))
            Dot(Color(theme.syntax.function))
        }
        BarText(theme.name)
        ArrowDown()
    }
}

/**
 * A button that opens one of the dock's dialogs. It shows its [text] and an
 * arrow, or only its [icon] on a [compact] dock.
 */
@Composable
private fun DialogButton(
    icon: ImageVector,
    text: String,
    compact: Boolean,
    onClick: () -> Unit
) {
    if (compact) BarIconButton(
        icon = icon,
        label = text,
        onClick = onClick
    ) else BarButton(onClick = onClick) {
        BarText(text)
        ArrowDown()
    }
}

/**
 * A square button showing only an [icon], used on a compact dock. It is
 * filled with the accent color while [selected].
 *
 * @param label what the button does, for screen readers
 */
@Composable
private fun BarIconButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    selected: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (selected) colors.onPrimary else colors.onSurface,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) colors.primary else colors.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(6.dp)
            .size(20.dp)
    )
}

/**
 * A button that opens the list of languages. It shows the code's language, or
 * only an icon on a [compact] dock.
 */
@Composable
private fun LanguageMenu(
    language: Language,
    compact: Boolean,
    onSelect: (Language) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        if (compact) BarIconButton(
            icon = Icons.Outlined.DataObject,
            label = stringResource(Res.string.editor_language),
            onClick = { expanded = true }
        ) else BarButton(onClick = { expanded = true }) {
            BarText(language.displayName)
            ArrowDown()
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            tipFrameLanguages.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.displayName) },
                    onClick = {
                        expanded = false
                        onSelect(item)
                    }
                )
            }
        }
    }
}

/** The font size between a minus and a plus button, tighter on a [compact] dock. */
@Composable
private fun FontSizeStepper(
    fontSize: Int,
    compact: Boolean,
    uiEvents: (TipFrameEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepButton(
            icon = Icons.Outlined.Remove,
            label = stringResource(Res.string.editor_zoom_out),
            compact = compact,
            onClick = { uiEvents(TipFrameEvent.ZoomOut) }
        )
        Text(
            text = fontSize.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(if (compact) 20.dp else 24.dp)
        )
        StepButton(
            icon = Icons.Outlined.Add,
            label = stringResource(Res.string.editor_zoom_in),
            compact = compact,
            onClick = { uiEvents(TipFrameEvent.ZoomIn) }
        )
    }
}

@Composable
private fun StepButton(
    icon: ImageVector,
    label: String,
    compact: Boolean,
    onClick: () -> Unit
) {
    Icon(
        imageVector = icon,
        contentDescription = label,
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(if (compact) 7.dp else 8.dp)
            .size(18.dp)
    )
}

/** The rounded, filled shape shared by the bar's buttons. */
@Composable
private fun BarButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun BarText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1
    )
}

@Composable
private fun ArrowDown() {
    Icon(
        imageVector = Icons.Outlined.KeyboardArrowDown,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(18.dp)
    )
}

@Composable
private fun BarDivider() {
    VerticalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        modifier = Modifier.height(24.dp)
    )
}

@Composable
private fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(color)
    )
}
