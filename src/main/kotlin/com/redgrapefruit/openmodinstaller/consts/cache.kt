package com.redgrapefruit.openmodinstaller.consts

import okhttp3.Cache
import java.io.File

const val CACHE_MAX_SIZE: Long = 1000L * 1024L * 1024L // 1GB max for heavy images

val assetCache = Cache(
    directory = File("./cache/"),
    maxSize = CACHE_MAX_SIZE
)
