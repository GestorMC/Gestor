package com.redgrapefruit.openmodinstaller.util

import com.redgrapefruit.openmodinstaller.JSON
import com.sun.security.auth.module.NTSystem
import com.redgrapefruit.openmodinstaller.data.distribution.DistributionSource
import com.redgrapefruit.openmodinstaller.data.mod.ReleaseType
import kotlinx.serialization.json.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object Settings {
    private val nt = NTSystem()

    /**
     * The target folder for downloading mods
     */
    var modsFolderField = "C:/Users/${nt.name}/AppData/Roaming/.minecraft/mods"

    /**
     * The folder for storing cache
     */
    var cacheFolderField = "C:/Users/${nt.name}/.openmodinstaller/cache"

    /**
     * Use unverified [DistributionSource]s
     */
    var useUnverifiedSources = true

    /**
     * Store caches or not.
     *
     * If not, the caches will immediately be deleted after use.
     */
    var storeCaches = true

    /**
     * Use autocomplete for connecting to distribution sources
     */
    var useAutocomplete = true

    /**
     * The target [ReleaseType] for downloading
     */
    var chosenReleaseType = ReleaseType.Stable

    /**
     * Loads settings
     */
    fun load() {
        // Check if the folder exists, create it
        val installerFolder = File(File(cacheFolderField).parent)
        if (!installerFolder.exists()) installerFolder.mkdirs()
        // Check if the settings JSON exists, create it
        val settingsFile = File("$installerFolder/settings.json")
        if (!settingsFile.exists()) {
            settingsFile.createNewFile()
            // Write defaults and return
            write(settingsFile)
            return
        }
        // Replace current with serialized
        FileInputStream(settingsFile).use { stream ->
            read(settingsFile)
        }
    }

    /**
     * Saves settings
     */
    fun save() {
        // Check if the folder exists, create it
        val installerFolder = File(File(cacheFolderField).parent)
        if (!installerFolder.exists()) installerFolder.mkdirs()
        // Save current into the file
        val settingsFile = File("$installerFolder/settings.json")
        FileOutputStream(settingsFile).use { stream ->
            write(settingsFile)
        }
    }

    private fun read(settingsFile: File) {
        FileInputStream(settingsFile).use { stream ->
            val txt = stream.readBytes().decodeToString()
            val json = JSON.decodeFromString(JsonObject.serializer(), txt)

            modsFolderField = json["modsFolder"]!!.jsonPrimitive.content
            cacheFolderField = json["cacheFolder"]!!.jsonPrimitive.content
            useUnverifiedSources = json["useUnverifiedSources"]!!.jsonPrimitive.boolean
            storeCaches = json["storeCaches"]!!.jsonPrimitive.boolean
            useAutocomplete = json["useAutocomplete"]!!.jsonPrimitive.boolean
            chosenReleaseType = ReleaseType.valueOf(json["chosenReleaseType"]!!.jsonPrimitive.content)
        }
    }

    private fun write(settingsFile: File) {
        val json = buildJsonObject {
            put("modsFolder", JsonPrimitive(modsFolderField))
            put("cacheFolder", JsonPrimitive(cacheFolderField))
            put("useUnverifiedSources", JsonPrimitive(useUnverifiedSources))
            put("storeCaches", JsonPrimitive(storeCaches))
            put("useAutocomplete", JsonPrimitive(useAutocomplete))
            put("chosenReleaseType", JsonPrimitive(chosenReleaseType.name))
        }
        FileOutputStream(settingsFile).use { stream ->
            stream.write(JSON.encodeToString(JsonObject.serializer(), json).encodeToByteArray())
        }
    }
}