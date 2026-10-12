package com.ma7moud3ly.tipframe.feature.tipframe.model

import com.ma7moud3ly.tipframe.model.AuthorAvatar
import com.ma7moud3ly.tipframe.model.BackgroundStyle
import com.ma7moud3ly.tipframe.model.WindowFrame
import io.ma7moud3ly.nemo.model.EditorTheme
import io.ma7moud3ly.nemo.model.Language

/**
 * What the user did: every control of the toolbar and the tools panel.
 *
 * Goes from the screen to TipFrameViewModel. [Export], [CopyImage] and [CopyLink]
 * are carried out by the screen, which holds the drawn image.
 */
sealed interface TipFrameEvent {
    // toolbar
    data object Export : TipFrameEvent
    data object CopyImage : TipFrameEvent
    data object CopyLink : TipFrameEvent
    data object Reset : TipFrameEvent
    data object ToggleAppTheme : TipFrameEvent

    // theme
    data class SelectTheme(val theme: EditorTheme) : TipFrameEvent

    // background
    data class SelectBackground(val style: BackgroundStyle) : TipFrameEvent
    data class SelectPalette(val index: Int) : TipFrameEvent
    data class SelectPadding(val padding: Int) : TipFrameEvent
    data object ToggleShadow : TipFrameEvent

    // frame
    data class SelectFrame(val frame: WindowFrame) : TipFrameEvent
    data class ChangeWidth(val width: Int) : TipFrameEvent
    data class ChangeTitle(val title: String) : TipFrameEvent

    // editor
    data class SelectLanguage(val language: Language) : TipFrameEvent
    data object ToggleLines : TipFrameEvent
    data object ZoomIn : TipFrameEvent
    data object ZoomOut : TipFrameEvent

    // author
    data class SelectAuthorAvatar(val avatar: AuthorAvatar) : TipFrameEvent
    data class ChangeAuthorName(val name: String) : TipFrameEvent
    data class ChangeAuthorHandle(val handle: String) : TipFrameEvent
    data object ToggleWatermark : TipFrameEvent
}
