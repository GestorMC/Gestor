package com.redgrapefruit.openmodinstaller.task

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.*

var cache = mutableMapOf<String, ImageBitmap>()

fun getCachedBitmap(url: String): ImageBitmap? = cache[url]

@OptIn(DelicateCoroutinesApi::class)
suspend fun getBitmapFromURL(url: String): ImageBitmap? {
    if (cache.contains(url))
        return cache[url]!!

    val data = GlobalScope.async {
        try {
            return@async org.jetbrains.skija.Image.makeFromEncoded(
                downloadFileRaw(url)!!.bytes()
            ).asImageBitmap()

        } catch (e: Exception) {
            println("Failed to Download file $url")
            println(e)
            return@async null
        }
    }

    val optionalImage = data.await();

    if(optionalImage !== null) cache[url] = optionalImage
    return optionalImage
}
