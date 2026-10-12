package com.ma7moud3ly.tipframe.model

import com.ma7moud3ly.tipframe.ui.theme.AppThemes
import io.ma7moud3ly.nemo.model.EditorTheme
import io.ma7moud3ly.nemo.model.Language

/**
 * Everything that makes up one image: the code and how it is presented.
 *
 * It is what the page address and the browser storage hold, so an image can be
 * reopened or shared as a link.
 *
 * @param code the code in the editor
 * @param language language used to highlight [code]
 * @param theme colors of the code frame: the window, the editor and the background palettes
 * @param title text in the window's title bar
 * @param frame the window drawn around the code
 * @param background what is painted behind the window
 * @param palette index of the background's colors, see `EditorTheme.palettes()`
 * @param padding space between the window and the edges of the image, in dp.
 *   With [BackgroundStyle.NONE] that space takes the window's own color. At 0
 *   the window fills the image and has square corners.
 * @param shadow whether the window casts a shadow on the background;
 *   not used when [background] is [BackgroundStyle.NONE]
 * @param width width of the image in dp; its height follows the code
 * @param lineNumbers whether the line numbers are shown
 * @param fontSize font size of the code
 * @param highlights the highlighted lines, one-based
 * @param authorAvatar the avatar of the author badge under the code, or [AuthorAvatar.NONE] for no badge
 * @param authorName the author's name
 * @param authorHandle the author's handle, such as a user name
 * @param watermark whether the app's logo and name are drawn, half see-through, over the bottom right corner of the code
 */
data class TipFrameConfig(
    val code: String = DEFAULT_CODE,
    val language: Language = Language.KOTLIN,
    val theme: EditorTheme = AppThemes.DEFAULT_FRAME_THEME,
    val title: String = "",
    val frame: WindowFrame = WindowFrame.MAC,
    val background: BackgroundStyle = BackgroundStyle.NONE,
    val palette: Int = 0,
    val padding: Int = 8,
    val shadow: Boolean = true,
    val width: Int = 800,
    val lineNumbers: Boolean = true,
    val fontSize: Int = 14,
    val highlights: List<Int> = emptyList(),
    val authorAvatar: AuthorAvatar = AuthorAvatar.NONE,
    val authorName: String = "",
    val authorHandle: String = "",
    val watermark: Boolean = false
) {
    companion object {
        /** The paddings offered to the user, in dp. */
        val PADDINGS = listOf(0, 8, 16, 32, 48, 64, 96)

        /** The padding a frame gets when a background is picked while it has none. */
        const val DEFAULT_BACKGROUND_PADDING = 48

        const val WIDTH_MIN = 400
        const val WIDTH_MAX = 1280

        const val FONT_SIZE_MIN = 10
        const val FONT_SIZE_MAX = 32

        /** How many background palettes a theme has. */
        const val PALETTE_COUNT = 6
    }
}

/** How many image pixels one dp of the image becomes when it is exported. */
const val EXPORT_SCALE = 2f

/** The code a new visitor starts with: a placeholder that says what to do with it. */
internal val DEFAULT_CODE = """
    /**
     * Welcome to TipFrame.
     * Replace this code with your own, then export it as an image.
     */
    fun main() {
        println("Hello, World!")
    }
""".trimIndent()
