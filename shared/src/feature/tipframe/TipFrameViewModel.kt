package com.ma7moud3ly.tipframe.feature.tipframe

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma7moud3ly.tipframe.feature.tipframe.model.TipFrameEvent
import com.ma7moud3ly.tipframe.managers.StorageManager
import com.ma7moud3ly.tipframe.managers.UrlCodec
import com.ma7moud3ly.tipframe.model.BackgroundStyle
import com.ma7moud3ly.tipframe.model.TipFrameConfig
import com.ma7moud3ly.tipframe.platform.pageUrl
import com.ma7moud3ly.tipframe.platform.readUrlHash
import com.ma7moud3ly.tipframe.platform.writeUrlHash
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/** How long the image has to stay unchanged before it is saved, in milliseconds. */
private const val SAVE_DELAY_MS = 300L

/** Space between the code and the edges of the editor. */
internal val EditorPadding = PaddingValues(start = 12.dp, top = 14.dp, end = 20.dp, bottom = 14.dp)

/**
 * Holds the image being edited and keeps it in the page address and in storage.
 *
 * On start the image is read from the page address, then from storage, then
 * from the defaults. Every later change is written back to both.
 */
class TipFrameViewModel(
    private val storageManager: StorageManager = StorageManager()
) : ViewModel() {

    /** Whether the page uses its dark theme. It does not change the code frame. */
    var darkTheme by mutableStateOf(storageManager.darkTheme)
        private set

    /**
     * The image's options. Its `code` and `highlights` are read from [codeState]
     * and its `fontSize` from [editorSettings]; [snapshot] puts them together.
     */
    var config by mutableStateOf(restore())
        private set

    /** The code in the editor. Replaced when the language changes. */
    var codeState by mutableStateOf(newCodeState(config))
        private set

    val editorSettings = EditorSettings(
        theme = config.theme,
        showLineNumbers = config.lineNumbers,
        showIndentGuides = false,
        fontSize = config.fontSize,
        enableAutocomplete = false,
        contentPadding = EditorPadding,
        showScrollbars = false
    )

    init {
        save()
    }

    fun onEvent(event: TipFrameEvent) {
        when (event) {
            is TipFrameEvent.SelectTheme -> update { copy(theme = event.theme) }
            is TipFrameEvent.SelectBackground -> update {
                // a background is only visible around the window, so one picked at padding 0 brings the usual padding
                val needsPadding = event.style != BackgroundStyle.NONE && padding == 0
                copy(
                    background = event.style,
                    padding = if (needsPadding) TipFrameConfig.DEFAULT_BACKGROUND_PADDING else padding
                )
            }
            is TipFrameEvent.SelectPalette -> update { copy(palette = event.index) }
            is TipFrameEvent.SelectPadding -> update { copy(padding = event.padding) }
            is TipFrameEvent.ToggleShadow -> update { copy(shadow = shadow.not()) }
            is TipFrameEvent.SelectFrame -> update { copy(frame = event.frame) }
            is TipFrameEvent.ChangeWidth -> update { copy(width = event.width) }
            is TipFrameEvent.ChangeTitle -> update { copy(title = event.title) }
            is TipFrameEvent.ToggleLines -> update { copy(lineNumbers = lineNumbers.not()) }
            is TipFrameEvent.SelectAuthorAvatar -> update { copy(authorAvatar = event.avatar) }
            is TipFrameEvent.ChangeAuthorName -> update { copy(authorName = event.name) }
            is TipFrameEvent.ChangeAuthorHandle -> update { copy(authorHandle = event.handle) }
            is TipFrameEvent.ToggleWatermark -> update { copy(watermark = watermark.not()) }
            is TipFrameEvent.ZoomIn -> setFontSize(editorSettings.fontSizeState.value + 1)
            is TipFrameEvent.ZoomOut -> setFontSize(editorSettings.fontSizeState.value - 1)
            is TipFrameEvent.SelectLanguage -> {
                val current = snapshot()
                update { copy(language = event.language) }
                codeState = newCodeState(current.copy(language = event.language))
            }

            is TipFrameEvent.ToggleAppTheme -> {
                darkTheme = darkTheme.not()
                storageManager.darkTheme = darkTheme
            }

            is TipFrameEvent.Reset -> {
                config = TipFrameConfig()
                codeState = newCodeState(config)
                applyToEditor()
                setFontSize(config.fontSize)
            }

            // carried out by the screen
            is TipFrameEvent.Export, is TipFrameEvent.CopyImage, is TipFrameEvent.CopyLink -> Unit
        }
    }

    /** The whole image as it is now: the options, the code and the highlighted lines. */
    fun snapshot(): TipFrameConfig = config.copy(
        code = BacktickGuard.strip(codeState.code),
        highlights = codeState.highlights.map { it.line }.sorted(),
        fontSize = editorSettings.fontSizeState.value
            .coerceIn(TipFrameConfig.FONT_SIZE_MIN, TipFrameConfig.FONT_SIZE_MAX)
    )

    /** Sets the font the editor draws the code with. */
    fun setCodeFont(font: FontFamily) {
        editorSettings.fontFamilyState.value = font
    }

    /** The address that reopens the image as it is now. */
    fun link(): String = pageUrl().substringBefore('#') + "#" + UrlCodec.encode(snapshot())

    /** The name of the exported file: the title, or "tipframe" when there is none. */
    fun fileName(): String {
        val name = config.title.trim()
            .replace(Regex("[^A-Za-z0-9._-]+"), "-")
            .trim('-')
        return name.ifEmpty { "tipframe" } + ".png"
    }

    private fun update(change: TipFrameConfig.() -> TipFrameConfig) {
        config = config.change()
        applyToEditor()
    }

    private fun setFontSize(size: Int) {
        editorSettings.setFontSize(size.coerceIn(TipFrameConfig.FONT_SIZE_MIN, TipFrameConfig.FONT_SIZE_MAX))
    }

    /**
     * Copies the theme and the line numbers option from [config] into
     * [editorSettings]. The font size lives in [editorSettings] alone, where
     * the zoom buttons and the pinch gesture change it.
     */
    private fun applyToEditor() {
        editorSettings.themeState.value = config.theme
        editorSettings.showLineNumbersState.value = config.lineNumbers
    }

    /** Writes the image to the page address and to storage a moment after each change. */
    @OptIn(FlowPreview::class)
    private fun save() {
        viewModelScope.launch {
            snapshotFlow { UrlCodec.encode(snapshot()) }
                .distinctUntilChanged()
                .debounce(SAVE_DELAY_MS.milliseconds)
                .collect { tipFrame ->
                    writeUrlHash(tipFrame)
                    storageManager.tipFrame = tipFrame
                }
        }
    }

    /** The image in the page address, else the one in storage, else the default one. */
    private fun restore(): TipFrameConfig =
        UrlCodec.decode(readUrlHash())
            ?: UrlCodec.decode(storageManager.tipFrame)
            ?: TipFrameConfig()
}

/** A new editor state holding the code and the highlighted lines of [config]. */
private fun newCodeState(config: TipFrameConfig) = CodeState(
    initialCode = config.code,
    language = config.language
).apply {
    config.highlights.forEach { toggleHighlight(it) }
}
