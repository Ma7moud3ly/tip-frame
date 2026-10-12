package com.ma7moud3ly.tipframe.model

/**
 * What is painted behind the window. [key] is its name in the page address.
 *
 * [NONE] paints the window's own color, so the image is one flat color around
 * the window. [DOTS], [GRID] and [LINES] are patterns drawn over a gradient.
 */
enum class BackgroundStyle(val key: String) {
    NONE("none"),
    SOLID("solid"),
    GRADIENT("gradient"),
    MESH("mesh"),
    DOTS("dots"),
    GRID("grid"),
    LINES("lines");

    companion object {
        fun fromKey(key: String?): BackgroundStyle? = entries.firstOrNull { it.key == key }
    }
}
