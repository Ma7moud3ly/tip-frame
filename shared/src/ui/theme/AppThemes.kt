package com.ma7moud3ly.tipframe.ui.theme

import io.ma7moud3ly.nemo.model.EditorTheme
import io.ma7moud3ly.nemo.model.EditorThemes

/**
 * The themes TipFrame uses.
 *
 * The page is painted with one of two themes, [DEFAULT_DARK] or
 * [DEFAULT_LIGHT]. The code frame takes any theme of [ALL].
 */
object AppThemes {

    /** The themes offered for the code frame: every theme shipped by nemo-editor. */
    val ALL: List<EditorTheme> = EditorThemes.ALL_THEMES

    /** The page's dark theme. */
    val DEFAULT_DARK = EditorThemes.VS_CODE_DARK

    /** The page's light theme. */
    val DEFAULT_LIGHT = EditorThemes.VS_CODE_LIGHT

    /** The code frame's theme before the user picks one. */
    val DEFAULT_FRAME_THEME = EditorThemes.NEMO_LIGHT

    /** The theme with this [key], or null when there is none. */
    fun fromKey(key: String?): EditorTheme? = ALL.firstOrNull { it.key == key }
}

/** The theme's name in the page address: "Nemo Dark" is "nemo-dark". */
val EditorTheme.key: String get() = name.lowercase().replace(' ', '-')
