package com.ma7moud3ly.tipframe.managers

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

/**
 * What the app remembers between visits. On the web it is kept in the
 * browser's local storage.
 */
class StorageManager(private val settings: Settings = Settings()) {

    /** The last image, in the form [UrlCodec] writes. Empty on a first visit. */
    var tipFrame: String
        get() = settings.getString(KEY_TIP_FRAME, "")
        set(value) {
            settings[KEY_TIP_FRAME] = value
        }

    /** Whether the page uses its dark theme. False on a first visit. */
    var darkTheme: Boolean
        get() = settings.getBoolean(KEY_DARK_THEME, false)
        set(value) {
            settings[KEY_DARK_THEME] = value
        }

    private companion object {
        const val KEY_TIP_FRAME = "tip_frame"
        const val KEY_DARK_THEME = "dark_theme"
    }
}
