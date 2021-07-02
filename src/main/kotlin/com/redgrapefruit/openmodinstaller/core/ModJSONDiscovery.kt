package com.redgrapefruit.openmodinstaller.core

import com.redgrapefruit.openmodinstaller.data.distribution.DistributionSource
import com.redgrapefruit.openmodinstaller.ui.Properties
import kotlinx.serialization.json.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.random.Random

object ModJSONDiscovery {
    val database: MutableMap<String, DistributionSource> = mutableMapOf()

    /**
     * Loads all user-defined sources
     */
    fun load(cacheFolderPath: String) {
        val sourceFile = initLocal()
        // Read everything from the JSON
        FileInputStream(sourceFile).use { stream ->
            val json = Json.parseToJsonElement(stream.readBytes().decodeToString())
            json.jsonObject.entries.forEach { entry ->
                discover(entry.value.jsonPrimitive.content, cacheFolderPath, false)
            }
        }
    }

    /**
     * Discovers a new source and adds it to the local DB if needed
     */
    fun discover(url: String, cacheFolderPath: String, tryAdd: Boolean) {
        // Download the JSON
        val cachePath = "$cacheFolderPath/dsc_json_${Random.nextInt(Integer.MAX_VALUE)}"
        File(cachePath).createNewFile()
        ModInstaller.downloadFile(url, cachePath)
        // Deserialize and add into the database
        FileInputStream(cachePath).use {
            val source = Json.decodeFromString(DistributionSource.serializer(), it.readBytes().decodeToString())
            database.put(url, source)
        }
        // Try to add to the local DB if needed
        if (tryAdd) {
            val sourceFile = initLocal()
            FileInputStream(sourceFile).use { stream ->
                val input = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
                val output = buildJsonObject {
                    // Add all + the new entry
                    input.entries.forEach { entry ->
                        put(entry.key, entry.value)
                    }
                    put(Random.nextInt(Int.MAX_VALUE).toString(), url)
                }
                // Rewrite
                FileOutputStream(sourceFile).use { outStream ->
                    outStream.write(Json.encodeToString(JsonObject.serializer(), output).encodeToByteArray())
                }
            }
        }
    }

    /**
     * Removes a source from the local DB and runtime DB
     */
    fun remove(url: String, cacheFolderPath: String) {
        // Remove from local
        database.forEach { (key, _) ->
            if (key == url) {
                database.remove(key)
            }
        }

        val sourceFile = initLocal()

        FileInputStream(sourceFile).use { stream ->
            val input = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
            val output = buildJsonObject {
                input.entries.forEach { entry ->
                    // Add all except for the deletion
                    if (entry.value.jsonPrimitive.content != url) {
                        put(entry.key, entry.value)
                    }
                }
            }
            // Rewrite
            FileOutputStream(sourceFile).use { outStream ->
                outStream.write(Json.encodeToString(JsonObject.serializer(), output).encodeToByteArray())
            }
        }
    }

    /**
     * Initializes the local DB with sources
     */
    private fun initLocal(): File {
        // Check if the folder exists, create it
        val installerFolder = File(File(Properties.cacheFolderField).parent)
        if (!installerFolder.exists()) installerFolder.mkdirs()
        // Check if the source file exists, create it
        val sourceFile = File("$installerFolder/sources.json")
        if (!sourceFile.exists()) {
            sourceFile.createNewFile()
            // Add a basic JSON structure
            FileOutputStream(sourceFile).use { stream ->
                stream.write("{\n\t\n}".toByteArray())
            }
        }
        return sourceFile
    }
}