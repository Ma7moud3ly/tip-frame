package com.ma7moud3ly.tipframe.model

/**
 * The window drawn around the code. [key] is its name in the page address.
 */
enum class WindowFrame(val key: String) {
    MAC("mac"),
    WINDOWS("windows"),
    TERMINAL("terminal"),
    BROWSER("browser"),
    PHONE("phone"),
    NONE("none");

    companion object {
        fun fromKey(key: String?): WindowFrame? = entries.firstOrNull { it.key == key }
    }
}
