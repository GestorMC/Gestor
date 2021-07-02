package com.redgrapefruit.openmodinstaller.core

import com.redgrapefruit.openmodinstaller.JSON
import com.redgrapefruit.openmodinstaller.data.mod.ReleaseEntry
import com.redgrapefruit.openmodinstaller.util.Properties
import com.redgrapefruit.openmodinstaller.util.Hash
import com.redgrapefruit.openmodinstaller.util.unjar
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.io.FileInputStream
import kotlin.random.Random

/**
 * Updates your mods
 */
object ModUpdater {
    /**
     * Checks if the mod has updates
     */
    fun hasUpdates(
        /**
         * The [ReleaseEntry] of this mod
         */
        entry: ReleaseEntry,
        /**
         * The path to the already downloaded JAR
         */
        jarPath: String,
        /**
         * The path to the cache folder
         */
        cacheFolderPath: String
    ): Boolean {
        // Download the latest JAR
        val latestJarPath = "$cacheFolderPath/upd_jar_${Random.nextInt(Int.MAX_VALUE)}.jar"
        File(latestJarPath).createNewFile()
        ModInstaller.downloadFile(entry.url, latestJarPath)

        // Perform a checksum check (SHA512)
        val currentChecksum = Hash.SHA512.checksum(jarPath).decodeToString()
        val latestChecksum = Hash.SHA512.checksum(latestJarPath).decodeToString()
        val areChecksumsDifferent = currentChecksum == latestChecksum

        // Perform a version check
        // Unjar
        val unjarPath = "$cacheFolderPath/upd_unjar_${Random.nextInt(Int.MAX_VALUE)}"
        File(unjarPath).mkdir()
        unjar(latestJarPath, unjarPath)
        // Extract the current version
        val currentVersionInput = FileInputStream("$unjarPath/fabric.mod.json")
        val currentVersion = JSON
            .parseToJsonElement(currentVersionInput.readBytes().decodeToString())
            .jsonObject["version"]?.jsonPrimitive?.content!!
        currentVersionInput.close()
        // Compare
        val areVersionsDifferent = currentVersion == entry.version

        // Delete caches if that option is enabled in the settings
        if (!Properties.storeCaches) {
            File(latestJarPath).delete()
            File(unjarPath).deleteRecursively()
        }

        // If any of these comparisons is successful, updates are available
        return areChecksumsDifferent || areVersionsDifferent
    }

    /**
     * Updates the mod to the latest version
     */
    fun update(
        /**
         * The target [ReleaseEntry]
         */
        entry: ReleaseEntry,
        /**
         * The mod ID of the mod
         */
        id: String,
        /**
         * The mods folder
         */
        modsFolder: String,
        /**
         * The current JAR path
         */
        jarPath: String
    ) {
        val file = File(jarPath)

        // Recreate the file
        if (file.exists()) file.delete()
        file.createNewFile()

        // Redownload the mod
        ModInstaller.downloadFile(entry.url, "$modsFolder/$id.jar")
    }
}