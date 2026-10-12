package com.ma7moud3ly.tipframe.platform

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The platforms the app is built for. [label] is the name to show a user.
 */
enum class Platform(val label: String) {
    Desktop("Desktop"),
    Web("Web"),

    Android("Android")
}

/** The platform this code is running on. */
expect val currentPlatform: Platform

val Platform.isWeb: Boolean get() = this == Platform.Web

val Platform.isDesktop: Boolean get() = this == Platform.Desktop

/**
 * The platform the surrounding composition renders for.
 *
 * Defaults to [currentPlatform]; provide it to render another platform's layout,
 * as in a `@Preview`.
 */
val LocalPlatform = staticCompositionLocalOf { currentPlatform }
