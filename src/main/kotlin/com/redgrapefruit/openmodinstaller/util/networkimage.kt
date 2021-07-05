@file:Suppress("FunctionName")

package com.redgrapefruit.openmodinstaller.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.redgrapefruit.openmodinstaller.core.ModInstaller
import org.jetbrains.skija.Image
import java.io.File
import java.io.FileInputStream
import kotlin.random.Random

@Composable
fun BitmapFromImageURL(url: String): ImageBitmap {
    val imageFolder = "${File(Settings.cacheFolderField).parent}/images/"

    val imageFolderFile = File(imageFolder)
    if (imageFolderFile.exists()) imageFolderFile.mkdirs()

    // TODO: Make a JSON (image_codecs.json) that will contain the cache number -> URL map to avoid unnecessary downloading
    val path = "$imageFolder/img_${Random.nextInt(Int.MAX_VALUE)}"
    File(path).createNewFile()

    ModInstaller.downloadFile(url, path)

    val raw: ByteArray
    FileInputStream(path).use { stream ->
        raw = stream.readBytes()
    }

    return Image.makeFromEncoded(raw).asImageBitmap()
}
