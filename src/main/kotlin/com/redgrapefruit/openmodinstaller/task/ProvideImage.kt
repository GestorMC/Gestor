package com.redgrapefruit.openmodinstaller.task

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.redgrapefruit.openmodinstaller.consts.assetCache
import okhttp3.*

class Event(val url: String): EventListener() {
    override fun cacheMiss(call: Call) {
        super.cacheMiss(call)
        println("Loading Asset: $url")
    }
}

suspend fun DownloadFile(url: String): ResponseBody? {
    val client: OkHttpClient = OkHttpClient.Builder().cache(assetCache).eventListener(Event(url)).build()

    val request = Request.Builder().url(url).build()
    return client.newCall(request).execute().body;
}

var cache = mutableMapOf<String, ImageBitmap>()

fun getCachedBitmap(url: String): ImageBitmap? = cache[url]
suspend fun getBitmapFromURL(url: String): ImageBitmap {
    if (cache.contains(url))
        return cache[url]!!

    val data = org.jetbrains.skija.Image.makeFromEncoded(
        DownloadFile(url)!!.bytes()
    ).asImageBitmap()

    cache[url] = data;
    return data
}