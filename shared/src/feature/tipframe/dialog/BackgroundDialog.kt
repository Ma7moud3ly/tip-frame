package com.ma7moud3ly.tipframe.feature.tipframe.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.tipframe.feature.tipframe.canvas.palettes
import com.ma7moud3ly.tipframe.feature.tipframe.canvas.tipFrameBackground
import com.ma7moud3ly.tipframe.feature.tipframe.model.TipFrameEvent
import com.ma7moud3ly.tipframe.model.BackgroundStyle
import com.ma7moud3ly.tipframe.model.TipFrameConfig
import com.ma7moud3ly.tipframe.resources.Res
import com.ma7moud3ly.tipframe.resources.background_colors
import com.ma7moud3ly.tipframe.resources.background_dots
import com.ma7moud3ly.tipframe.resources.background_gradient
import com.ma7moud3ly.tipframe.resources.background_grid
import com.ma7moud3ly.tipframe.resources.background_lines
import com.ma7moud3ly.tipframe.resources.background_mesh
import com.ma7moud3ly.tipframe.resources.background_none
import com.ma7moud3ly.tipframe.resources.background_padding
import com.ma7moud3ly.tipframe.resources.background_shadow
import com.ma7moud3ly.tipframe.resources.background_solid
import com.ma7moud3ly.tipframe.resources.background_style
import com.ma7moud3ly.tipframe.resources.tools_background
import com.ma7moud3ly.tipframe.ui.components.MyDialog
import com.ma7moud3ly.tipframe.ui.components.ToolChip
import com.ma7moud3ly.tipframe.ui.components.ToolLabel
import com.ma7moud3ly.tipframe.ui.components.ToolSwitch
import com.ma7moud3ly.tipframe.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun BackgroundOptionsPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            BackgroundOptions(
                config = TipFrameConfig(background = BackgroundStyle.MESH),
                uiEvents = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun BackgroundOptionsPreviewLightNone() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            BackgroundOptions(
                config = TipFrameConfig(background = BackgroundStyle.NONE),
                uiEvents = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

/**
 * Dialog with the background's options: its style, its colors, the padding
 * around the window and the window's shadow. With no background only the
 * style and the padding are offered. Each choice is applied at once; the
 * dialog stays open until it is closed.
 */
@Composable
internal fun BackgroundDialog(
    config: TipFrameConfig,
    uiEvents: (TipFrameEvent) -> Unit,
    onDismiss: () -> Unit
) {
    MyDialog(
        title = stringResource(Res.string.tools_background),
        onDismiss = onDismiss
    ) {
        BackgroundOptions(
            config = config,
            uiEvents = uiEvents,
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BackgroundOptions(
    config: TipFrameConfig,
    uiEvents: (TipFrameEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val palettes = remember(config.theme) { config.theme.palettes() }
    val colors = palettes[config.palette.coerceIn(0, palettes.lastIndex)]

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ToolLabel(stringResource(Res.string.background_style))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BackgroundStyle.entries.forEach { style ->
                StyleCard(
                    style = style,
                    colors = colors,
                    base = Color(config.theme.background),
                    isSelected = style == config.background,
                    onClick = { uiEvents(TipFrameEvent.SelectBackground(style)) }
                )
            }
        }

        // colors and a shadow belong to a background; the padding is offered either way,
        // since without a background it is filled with the window's own color
        val hasBackground = config.background != BackgroundStyle.NONE

        if (hasBackground) {
            ToolLabel(stringResource(Res.string.background_colors))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                palettes.forEachIndexed { index, palette ->
                    PaletteDot(
                        colors = palette,
                        isSelected = index == config.palette,
                        onClick = { uiEvents(TipFrameEvent.SelectPalette(index)) }
                    )
                }
            }
        }

        ToolLabel(stringResource(Res.string.background_padding))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TipFrameConfig.PADDINGS.forEach { padding ->
                ToolChip(
                    text = padding.toString(),
                    selected = padding == config.padding,
                    onClick = { uiEvents(TipFrameEvent.SelectPadding(padding)) }
                )
            }
        }

        if (hasBackground) ToolSwitch(
            label = stringResource(Res.string.background_shadow),
            checked = config.shadow,
            onCheckedChange = { uiEvents(TipFrameEvent.ToggleShadow) }
        )
    }
}

/** A background style as a card: a sample painted in that style, over its name. */
@Composable
private fun StyleCard(
    style: BackgroundStyle,
    colors: List<Color>,
    base: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    Column(
        modifier = Modifier.width(96.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .tipFrameBackground(style = style, colors = colors, base = base)
                .border(if (isSelected) 2.dp else 1.dp, borderColor, shape)
                .clickable(onClick = onClick)
        )
        Text(
            text = stringResource(
                when (style) {
                    BackgroundStyle.NONE -> Res.string.background_none
                    BackgroundStyle.SOLID -> Res.string.background_solid
                    BackgroundStyle.GRADIENT -> Res.string.background_gradient
                    BackgroundStyle.MESH -> Res.string.background_mesh
                    BackgroundStyle.DOTS -> Res.string.background_dots
                    BackgroundStyle.GRID -> Res.string.background_grid
                    BackgroundStyle.LINES -> Res.string.background_lines
                }
            ),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

/** A palette as a circle filled with a gradient of its first two colors. */
@Composable
private fun PaletteDot(
    colors: List<Color>,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent
    Box(
        modifier = Modifier
            .size(30.dp)
            .border(2.dp, borderColor, CircleShape)
            .padding(4.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(colors.take(2)))
            .clickable(onClick = onClick)
    )
}
