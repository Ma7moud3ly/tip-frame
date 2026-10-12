package com.ma7moud3ly.tipframe

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.ma7moud3ly.tipframe.resources.Res
import com.ma7moud3ly.tipframe.resources.app_icon
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    // the window opens filling the screen, with its title bar and buttons kept
    val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "TipFrame",
        icon = painterResource(Res.drawable.app_icon)
    ) {
        TipFrameApp()
    }
}
