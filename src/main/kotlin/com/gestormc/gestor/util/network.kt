package com.gestormc.gestor.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.gestormc.gestor.const.assetCache
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/**
 * An event handling cache misses for use in OkHttp
 */
private class Event(
    /**
     * The requested download URL
     */
    val url: String) : EventListener() {

    override fun cacheMiss(call: Call) {
        super.cacheMiss(call)

        println("Loading asset: $url")
    }

    override fun cacheHit(call: Call, response: Response) = hit()
    override fun cacheConditionalHit(call: Call, cachedResponse: Response) = hit()

    private fun hit() {
        println("Loading asset from cache: $url")
    }
}


/**
 * Raw [downloadFile] that returns a raw HTTP [ResponseBody]
 */
fun downloadFileRaw(
    input: String,
): ResponseBody? {
    // Init OkHttp client
    val client = OkHttpClient.Builder().cache(assetCache).eventListener(Event(input)).build()
    // Make request
    val request = Request.Builder().url(input).build()
    // Call
    return client.newCall(request).execute().body
}

/**
 * Downloads a file from the Internet using the OkHttp client
 */
fun downloadFile(
    /**
     * The input URL. Recommended to provide full URLs (e.g. not `github.com`, but `https://www.github.com`)
     */
    input: String,
    /**
     * The output URI
     */
    output: String,
) {
    val outputFile = File(output)
    outputFile.mkdirs()

    Files.copy(downloadFileRaw(input)!!.byteStream(), Path.of(output), StandardCopyOption.REPLACE_EXISTING)
}

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

    val optionalImage = data.await()

    if(optionalImage !== null) cache[url] = optionalImage
    return optionalImage
}
