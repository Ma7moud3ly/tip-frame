package com.ma7moud3ly.tipframe.platform

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toAwtImage
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.File

// The desktop app has no page address: the image is kept in storage only.

actual fun readUrlHash(): String = ""

actual fun writeUrlHash(hash: String) = Unit

actual fun pageUrl(): String = ""

actual fun copyText(text: String) {
    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
}

/** Saves [image] as a PNG in the user's Downloads folder, or in the home folder when there is none. */
actual fun downloadImage(image: ImageBitmap, fileName: String) {
    val bytes = Image.makeFromBitmap(image.asSkiaBitmap())
        .encodeToData(EncodedImageFormat.PNG)
        ?.bytes ?: return
    val home = File(System.getProperty("user.home"))
    val folder = File(home, "Downloads").takeIf { it.isDirectory } ?: home
    File(folder, fileName).writeBytes(bytes)
}

actual fun copyImage(image: ImageBitmap) {
    val awtImage = image.toAwtImage()
    val transferable = object : Transferable {
        override fun getTransferDataFlavors(): Array<DataFlavor> = arrayOf(DataFlavor.imageFlavor)

        override fun isDataFlavorSupported(flavor: DataFlavor): Boolean =
            flavor == DataFlavor.imageFlavor

        override fun getTransferData(flavor: DataFlavor): Any {
            if (flavor != DataFlavor.imageFlavor) throw UnsupportedFlavorException(flavor)
            return awtImage
        }
    }
    Toolkit.getDefaultToolkit().systemClipboard.setContents(transferable, null)
}
