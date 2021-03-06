package com.gestormc.gestor.const

import okhttp3.Cache
import java.io.File

const val CACHE_MAX_SIZE: Long = 5000L * 1024L * 1024L // 5GB max for heavy data, Java ZIPs, TAR.GZs etc.

val assetCache = Cache(
    directory = File("./cache/"),
    maxSize = CACHE_MAX_SIZE
)
