package com.ma7moud3ly.tipframe.platform

import androidx.compose.ui.graphics.ImageBitmap

// The Android platform only renders the IDE previews, so there is no page to read or write.

actual fun readUrlHash(): String = ""

actual fun writeUrlHash(hash: String) = Unit

actual fun pageUrl(): String = ""

actual fun copyText(text: String) = Unit

actual fun downloadImage(image: ImageBitmap, fileName: String) = Unit

actual fun copyImage(image: ImageBitmap) = Unit
