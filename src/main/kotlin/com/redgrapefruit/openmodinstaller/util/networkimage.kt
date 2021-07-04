@file:Suppress("FunctionName")

package com.redgrapefruit.openmodinstaller.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.redgrapefruit.openmodinstaller.core.ModInstaller
import java.io.File

@Composable
fun BitmapFromImageURL(url: String): ImageBitmap {
    val path = "image_resource_$url"
    val file = resourceFile(path)

    if (!file.exists()) {
        file.createNewFile()
        ModInstaller.downloadFile(url, file.absolutePath)
    }

    return imageResource(path)
}

private fun resourceFile(name: String): File {
    val loader = ClassLoader.getSystemClassLoader()

    return File(loader.getResource(name)!!.toURI())
}
