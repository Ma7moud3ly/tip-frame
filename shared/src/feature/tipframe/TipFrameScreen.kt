package com.ma7moud3ly.tipframe.feature.tipframe

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ma7moud3ly.tipframe.feature.tipframe.model.TipFrameEvent
import com.ma7moud3ly.tipframe.platform.copyImage
import com.ma7moud3ly.tipframe.platform.copyText
import com.ma7moud3ly.tipframe.platform.downloadImage
import com.ma7moud3ly.tipframe.resources.Res
import com.ma7moud3ly.tipframe.resources.msg_image_copied
import com.ma7moud3ly.tipframe.resources.msg_image_saved
import com.ma7moud3ly.tipframe.resources.msg_link_copied
import com.ma7moud3ly.tipframe.ui.theme.codeFontFamily
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

@Composable
fun TipFrameScreen(
    viewModel: TipFrameViewModel = viewModel { TipFrameViewModel() }
) {
    val graphicsLayer = rememberGraphicsLayer()
    val snackBarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val codeFont = codeFontFamily()

    // The font arrives once its files are loaded; pass each new value to the editor.
    LaunchedEffect(codeFont) {
        viewModel.setCodeFont(codeFont)
    }

    /** Shows [message] at the bottom of the page, replacing the one on screen. */
    fun showMessage(message: StringResource) {
        scope.launch {
            snackBarHostState.currentSnackbarData?.dismiss()
            snackBarHostState.showSnackbar(getString(message))
        }
    }

    /**
     * Hands the image as an image to [use], then shows [message].
     *
     * The editor loses focus first, and two frames pass, so the image is
     * drawn without the caret.
     */
    fun capture(message: StringResource, use: (ImageBitmap) -> Unit) {
        scope.launch {
            focusManager.clearFocus()
            repeat(2) { withFrameNanos { } }
            use(graphicsLayer.toImageBitmap())
            showMessage(message)
        }
    }

    TipFrameScreenContent(
        config = viewModel.config,
        codeState = viewModel.codeState,
        settings = viewModel.editorSettings,
        graphicsLayer = graphicsLayer,
        snackBarHostState = snackBarHostState,
        uiEvents = {
            when (it) {
                is TipFrameEvent.Export -> capture(Res.string.msg_image_saved) { image ->
                    downloadImage(image, viewModel.fileName())
                }

                is TipFrameEvent.CopyImage -> capture(Res.string.msg_image_copied) { image ->
                    copyImage(image)
                }

                is TipFrameEvent.CopyLink -> {
                    copyText(viewModel.link())
                    showMessage(Res.string.msg_link_copied)
                }

                else -> viewModel.onEvent(it)
            }
        }
    )
}
