package com.redgrapefruit.openmodinstaller.core

import com.redgrapefruit.openmodinstaller.data.distribution.DistributionSource
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import kotlin.random.Random

object ModJSONDiscovery {
    private val database: MutableSet<DistributionSource> = mutableSetOf()

    fun discover(url: String, cacheFolderPath: String) {
        // Download the JSON
        val cachePath = "$cacheFolderPath/dsc_json_${Random.nextInt(Integer.MAX_VALUE)}"
        File(cachePath).createNewFile()
        ModInstaller.downloadFile(url, cachePath)
        // Deserialize and add into the database
        FileInputStream(cachePath).use {
            val source = Json.decodeFromString(DistributionSource.serializer(), it.readBytes().decodeToString())
            database += source
        }
    }
}