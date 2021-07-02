package com.redgrapefruit.openmodinstaller.core

import com.redgrapefruit.openmodinstaller.data.distribution.DistributionSource
import com.redgrapefruit.openmodinstaller.ui.Properties
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.random.Random

object ModJSONDiscovery {
    val database: MutableSet<DistributionSource> = mutableSetOf()

    /**
     * Loads all user-defined sources
     */
    fun load(cacheFolderPath: String) {
        // Check if the folder exists, create it
        val installerFolder = File("C:/Users/${Properties.nt.name}/.openmodinstaller")
        if (!installerFolder.exists()) installerFolder.mkdirs()
        // Check if the source file exists, create it
        val sourceFile = File("$installerFolder/sources.json")
        if (!sourceFile.exists()) {
            sourceFile.createNewFile()
            // Add a basic JSON structure
            FileOutputStream(sourceFile).use {
                it.write("{\n\t\n}".toByteArray())
            }
        }
        // Read everything from the JSON
        FileInputStream(sourceFile).use {
            val json = Json.parseToJsonElement(it.readBytes().decodeToString())
            json.jsonObject.entries.forEach { entry ->
                discover(entry.value.jsonPrimitive.content, cacheFolderPath)
            }
        }
    }

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