package com.ma7moud3ly.tipframe.managers

import com.ma7moud3ly.tipframe.model.AuthorAvatar
import com.ma7moud3ly.tipframe.model.BackgroundStyle
import com.ma7moud3ly.tipframe.model.TipFrameConfig
import com.ma7moud3ly.tipframe.model.WindowFrame
import com.ma7moud3ly.tipframe.model.key
import com.ma7moud3ly.tipframe.model.languageFromKey
import com.ma7moud3ly.tipframe.ui.theme.AppThemes
import com.ma7moud3ly.tipframe.ui.theme.key
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Turns a [TipFrameConfig] into the text after `#` in the page address, and back.
 *
 * The text is a list of `key=value` pairs joined by `&`, for example
 * `theme=nemo-dark&language=kotlin&padding=48&code=ZnVuIG1haW4oKQ`.
 * The code is Base64 (URL-safe, without padding); other values are
 * percent-encoded, with a space written as `+`.
 */
@OptIn(ExperimentalEncodingApi::class)
object UrlCodec {

    private val base64 = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)

    /** Writes [config] as `key=value` pairs. Empty values are left out, except the code. */
    fun encode(config: TipFrameConfig): String = listOf(
        KEY_THEME to config.theme.key,
        KEY_LANGUAGE to config.language.key,
        KEY_TITLE to config.title,
        KEY_FRAME to config.frame.key,
        KEY_BACKGROUND to config.background.key,
        KEY_PALETTE to config.palette.toString(),
        KEY_PADDING to config.padding.toString(),
        KEY_SHADOW to config.shadow.toString(),
        KEY_WIDTH to config.width.toString(),
        KEY_LINES to config.lineNumbers.toString(),
        KEY_FONT to config.fontSize.toString(),
        KEY_HIGHLIGHTS to config.highlights.joinToString(","),
        KEY_AVATAR to config.authorAvatar.key,
        KEY_NAME to config.authorName,
        KEY_HANDLE to config.authorHandle,
        KEY_WATERMARK to config.watermark.toString(),
        KEY_CODE to base64.encode(config.code.encodeToByteArray())
    ).filter { (key, value) -> key == KEY_CODE || value.isNotEmpty() }
        .joinToString("&") { (key, value) -> "$key=${escape(value)}" }

    /**
     * Reads a config from [text].
     *
     * A missing or unreadable value takes its default from [TipFrameConfig].
     * Returns null when [text] holds no pairs at all.
     */
    fun decode(text: String): TipFrameConfig? {
        val values = text.removePrefix("#")
            .split('&')
            .mapNotNull { pair ->
                val index = pair.indexOf('=')
                if (index <= 0) null
                else pair.take(index) to unescape(pair.substring(index + 1))
            }
            .toMap()
        if (values.isEmpty()) return null

        val defaults = TipFrameConfig()
        return TipFrameConfig(
            code = values[KEY_CODE]?.let(::decodeCode) ?: defaults.code,
            language = languageFromKey(values[KEY_LANGUAGE]) ?: defaults.language,
            theme = AppThemes.fromKey(values[KEY_THEME]) ?: defaults.theme,
            title = values[KEY_TITLE] ?: defaults.title,
            frame = WindowFrame.fromKey(values[KEY_FRAME]) ?: defaults.frame,
            background = BackgroundStyle.fromKey(values[KEY_BACKGROUND]) ?: defaults.background,
            palette = values[KEY_PALETTE]?.toIntOrNull()
                ?.coerceIn(0, TipFrameConfig.PALETTE_COUNT - 1) ?: defaults.palette,
            padding = values[KEY_PADDING]?.toIntOrNull()
                ?.coerceIn(TipFrameConfig.PADDINGS.first(), TipFrameConfig.PADDINGS.last()) ?: defaults.padding,
            width = values[KEY_WIDTH]?.toIntOrNull()
                ?.coerceIn(TipFrameConfig.WIDTH_MIN, TipFrameConfig.WIDTH_MAX) ?: defaults.width,
            shadow = values[KEY_SHADOW]?.toBooleanStrictOrNull() ?: defaults.shadow,
            lineNumbers = values[KEY_LINES]?.toBooleanStrictOrNull() ?: defaults.lineNumbers,
            fontSize = values[KEY_FONT]?.toIntOrNull()
                ?.coerceIn(TipFrameConfig.FONT_SIZE_MIN, TipFrameConfig.FONT_SIZE_MAX) ?: defaults.fontSize,
            highlights = values[KEY_HIGHLIGHTS]?.split(',')
                ?.mapNotNull { it.toIntOrNull() }
                ?.filter { it > 0 } ?: defaults.highlights,
            authorAvatar = AuthorAvatar.fromKey(values[KEY_AVATAR]) ?: defaults.authorAvatar,
            authorName = values[KEY_NAME] ?: defaults.authorName,
            authorHandle = values[KEY_HANDLE] ?: defaults.authorHandle,
            watermark = values[KEY_WATERMARK]?.toBooleanStrictOrNull() ?: defaults.watermark
        )
    }

    /** The code held by a Base64 [value], or null when it can't be read. */
    private fun decodeCode(value: String): String? = try {
        base64.decode(value).decodeToString()
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }

    /** Percent-encodes [value]: letters, digits and `-_.~` stay, a space becomes `+`. */
    private fun escape(value: String): String = buildString {
        value.encodeToByteArray().forEach { byte ->
            val code = byte.toInt() and 0xFF
            when (val char = code.toChar()) {
                in 'a'..'z', in 'A'..'Z', in '0'..'9', in UNRESERVED -> append(char)
                ' ' -> append('+')
                else -> append('%').append(HEX[code shr 4]).append(HEX[code and 0x0F])
            }
        }
    }

    /** Reverses [escape]. Text that is not encoded is kept as it is. */
    private fun unescape(value: String): String {
        val bytes = mutableListOf<Byte>()
        val literal = StringBuilder()

        fun flushLiteral() {
            if (literal.isEmpty()) return
            bytes.addAll(literal.toString().encodeToByteArray().asList())
            literal.clear()
        }

        var index = 0
        while (index < value.length) {
            val char = value[index]
            val escaped = if (char == '%' && index + 2 < value.length) {
                value.substring(index + 1, index + 3).toIntOrNull(16)
            } else null
            when {
                escaped != null -> {
                    flushLiteral()
                    bytes.add(escaped.toByte())
                    index += 3
                }

                char == '+' -> {
                    literal.append(' ')
                    index++
                }

                else -> {
                    literal.append(char)
                    index++
                }
            }
        }
        flushLiteral()
        return bytes.toByteArray().decodeToString()
    }

    private const val UNRESERVED = "-_.~"
    private const val HEX = "0123456789ABCDEF"

    private const val KEY_THEME = "theme"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_TITLE = "title"
    private const val KEY_CODE = "code"
    private const val KEY_FRAME = "frame"
    private const val KEY_BACKGROUND = "background"
    private const val KEY_PALETTE = "palette"
    private const val KEY_PADDING = "padding"
    private const val KEY_SHADOW = "shadow"
    private const val KEY_WIDTH = "width"
    private const val KEY_LINES = "lines"
    private const val KEY_FONT = "font"
    private const val KEY_HIGHLIGHTS = "highlights"
    private const val KEY_AVATAR = "avatar"
    private const val KEY_NAME = "name"
    private const val KEY_HANDLE = "handle"
    private const val KEY_WATERMARK = "watermark"
}
