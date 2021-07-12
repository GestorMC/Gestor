package com.redgrapefruit.openmodinstaller.consts

import okhttp3.Cache
import java.io.File

val assetCache = Cache(
    directory = File("./cache/", "http_cache"),
    maxSize = 1000L * 1024L * 1024L // 1000 MiB
)