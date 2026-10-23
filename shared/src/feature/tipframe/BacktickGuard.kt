package com.ma7moud3ly.tipframe.feature.tipframe

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.ma7moud3ly.nemo.InternalNemoApi
import io.ma7moud3ly.nemo.model.CodeState

/**
 * Works around a crash in nemo-editor 1.1.0.
 *
 * Its tokenizer, used for every language but Python, reads a backtick as the
 * start of a quoted name, as in `` `my name` ``, and throws when that name is
 * empty: when the backtick is followed by another backtick, as in a Markdown
 * code fence, or ends the code. It throws while the editor is drawn, so the
 * app stops responding, and again on every reload, since the code is saved.
 *
 * The editor's copy of the code gets an invisible [MARK] after each such
 * backtick, which gives the name a character. The mark is not part of the
 * image's code: [strip] takes it out before the code is saved or shared.
 *
 * Remove this once nemo-editor no longer throws on an empty quoted name.
 */
internal object BacktickGuard {

    /**
     * A zero width space. The code font has a glyph for it, so it is drawn as
     * nothing, on the screen and in the exported image.
     */
    private const val MARK = '​'

    /** [text] without the marks. */
    fun strip(text: String): String = text.replace(MARK.toString(), "")

    /**
     * [value] with a mark after each backtick that is followed by another
     * backtick or ends the text, and no other marks. The caret and the
     * selection keep their place in the text, before a mark that is added.
     *
     * Returns null when [value] already is that way.
     */
    fun guard(value: TextFieldValue): TextFieldValue? {
        val text = value.text
        val result = StringBuilder(text.length + 1)
        // where each offset of [text] lands in the result
        val offsets = IntArray(text.length + 1)
        // a backtick was the last character kept and needs a mark after it
        var markPending = false

        for (index in text.indices) {
            offsets[index] = result.length
            val char = text[index]
            if (char == MARK) continue
            if (markPending) {
                result.append(MARK)
                markPending = false
            }
            result.append(char)
            markPending = char == '`' && nextKept(text, index + 1).let { it == null || it == '`' }
        }
        offsets[text.length] = result.length
        if (markPending) result.append(MARK)

        val guarded = result.toString()
        if (guarded == text) return null

        fun TextRange.moved() = TextRange(
            offsets[start.coerceIn(0, text.length)],
            offsets[end.coerceIn(0, text.length)]
        )
        return TextFieldValue(
            text = guarded,
            selection = value.selection.moved(),
            composition = value.composition?.moved()
        )
    }

    /** The first character of [text] from [from] on that is not a mark, or null when there is none. */
    private fun nextKept(text: String, from: Int): Char? {
        for (index in from until text.length) {
            if (text[index] != MARK) return text[index]
        }
        return null
    }
}

/**
 * Applies [BacktickGuard] to the code of this state. Called while composing,
 * before the editor reads the code, so the editor never tokenizes the code
 * without its marks.
 */
@OptIn(InternalNemoApi::class)
internal fun CodeState.guardBackticks() {
    BacktickGuard.guard(value)?.let { updateCodeValue(it) }
}
