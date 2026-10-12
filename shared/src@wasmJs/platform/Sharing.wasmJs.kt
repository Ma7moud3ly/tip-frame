package com.ma7moud3ly.tipframe.platform

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.browser.window
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

actual fun readUrlHash(): String = window.location.hash.removePrefix("#")

@OptIn(ExperimentalWasmJsInterop::class)
actual fun writeUrlHash(hash: String) {
    window.history.replaceState(null, "", "#$hash")
}

actual fun pageUrl(): String = window.location.href

actual fun copyText(text: String) = jsCopyText(text)

actual fun downloadImage(image: ImageBitmap, fileName: String) {
    val png = image.toPngBase64() ?: return
    jsDownloadPng(png, fileName)
}

actual fun copyImage(image: ImageBitmap) {
    val png = image.toPngBase64() ?: return
    jsCopyPng(png)
}

/** This image as a PNG, Base64 encoded. Null when the image can't be encoded. */
@OptIn(ExperimentalEncodingApi::class)
private fun ImageBitmap.toPngBase64(): String? {
    val bytes = Image.makeFromBitmap(asSkiaBitmap())
        .encodeToData(EncodedImageFormat.PNG)
        ?.bytes ?: return null
    return Base64.Default.encode(bytes)
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun jsCopyText(text: String) {
    js("navigator.clipboard.writeText(text).catch(() => {});")
}

/** Builds a PNG file from [base64] and starts a download named [fileName]. */
@OptIn(ExperimentalWasmJsInterop::class)
private fun jsDownloadPng(base64: String, fileName: String) {
    js(
        """
        const binary = atob(base64);
        const bytes = new Uint8Array(binary.length);
        for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
        const url = URL.createObjectURL(new Blob([bytes], { type: 'image/png' }));
        const link = document.createElement('a');
        link.href = url;
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
        """
    )
}

/** Builds a PNG from [base64] and writes it to the clipboard. */
@OptIn(ExperimentalWasmJsInterop::class)
private fun jsCopyPng(base64: String) {
    js(
        """
        const binary = atob(base64);
        const bytes = new Uint8Array(binary.length);
        for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
        const blob = new Blob([bytes], { type: 'image/png' });
        navigator.clipboard.write([new ClipboardItem({ 'image/png': blob })]).catch(() => {});
        """
    )
}
