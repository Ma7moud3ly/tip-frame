package com.ma7moud3ly.tipframe.platform

import androidx.compose.ui.graphics.ImageBitmap

/** The text after `#` in the page address, without the `#`. */
expect fun readUrlHash(): String

/** Replaces the text after `#` in the page address, without adding a history entry. */
expect fun writeUrlHash(hash: String)

/** The full address of the page, including the text after `#`. */
expect fun pageUrl(): String

/** Puts [text] on the clipboard. */
expect fun copyText(text: String)

/** Saves [image] as a PNG file named [fileName]. */
expect fun downloadImage(image: ImageBitmap, fileName: String)

/** Puts [image] on the clipboard as a PNG. */
expect fun copyImage(image: ImageBitmap)
