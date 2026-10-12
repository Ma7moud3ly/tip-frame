package com.ma7moud3ly.tipframe.feature.tipframe

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.tooling.preview.Devices.DESKTOP
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.tipframe.feature.tipframe.canvas.TipFrameCanvas
import com.ma7moud3ly.tipframe.feature.tipframe.model.TipFrameEvent
import com.ma7moud3ly.tipframe.model.TipFrameConfig
import com.ma7moud3ly.tipframe.ui.theme.AppTheme
import com.ma7moud3ly.tipframe.ui.theme.AppThemes
import com.ma7moud3ly.tipframe.ui.theme.codeFontFamily
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorTheme

/** A window narrower than this gets the compact page. */
private val CompactWidth = 840.dp

@Preview
@Composable
private fun TipFrameScreenPreviewLight() {
    TipFrameScreenPreview(AppThemes.DEFAULT_LIGHT)
}

@Preview
@Composable
private fun TipFrameScreenPreviewDark() {
    TipFrameScreenPreview(AppThemes.DEFAULT_DARK)
}

@Preview(device = DESKTOP, widthDp = 1280)
@Composable
private fun TipFrameScreenPreviewDarkDesktop() {
    TipFrameScreenPreview(AppThemes.DEFAULT_DARK)
}

@Composable
private fun TipFrameScreenPreview(theme: EditorTheme) {
    val config = TipFrameConfig(theme = theme)
    val font = codeFontFamily()
    AppTheme(theme = theme) {
        TipFrameScreenContent(
            config = config,
            codeState = remember { CodeState(config.code, config.language) },
            settings = remember(font) { EditorSettings(theme = theme, fontFamily = font) },
            graphicsLayer = rememberGraphicsLayer(),
            snackBarHostState = remember { SnackbarHostState() },
            uiEvents = {}
        )
    }
}

/**
 * The whole page: the top bar, then the image with the dock, which holds
 * every option, floating at the bottom center of its area.
 *
 * Below [CompactWidth] the page is compact: the buttons of the top bar and of
 * the dock show icons only, and the image is laid out no wider than the page.
 *
 * @param graphicsLayer receives the drawn image, for exporting it
 */
@Composable
fun TipFrameScreenContent(
    config: TipFrameConfig,
    codeState: CodeState,
    settings: EditorSettings,
    graphicsLayer: GraphicsLayer,
    snackBarHostState: SnackbarHostState,
    uiEvents: (TipFrameEvent) -> Unit
) {
    val fontSize = settings.fontSizeState.value
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val isCompact = maxWidth < CompactWidth
            Column(modifier = Modifier.fillMaxSize()) {
                TipFrameToolbar(
                    showLabels = isCompact.not(),
                    uiEvents = uiEvents
                )
                HorizontalDivider(color = dividerColor)

                TipFrameCanvas(
                    config = config,
                    codeState = codeState,
                    settings = settings,
                    graphicsLayer = graphicsLayer,
                    onTitleChange = { uiEvents(TipFrameEvent.ChangeTitle(it)) },
                    onWidthChange = { uiEvents(TipFrameEvent.ChangeWidth(it)) },
                    compact = isCompact,
                    dock = {
                        TipFrameEditorBar(
                            config = config,
                            fontSize = fontSize,
                            compact = isCompact,
                            uiEvents = uiEvents
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}
