package com.redgrapefruit.openmodinstaller.core

import com.redgrapefruit.openmodinstaller.data.ReleaseEntry
import com.redgrapefruit.openmodinstaller.util.Hash
import java.io.File
import kotlin.random.Random

/**
 * Updates your mods
 */
@Deprecated("This system is deprecated. Currently being migrated to the Task architecture")
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
        val currentChecksum = Hash.checksum(jarPath).decodeToString()
        val latestChecksum = Hash.checksum(latestJarPath).decodeToString()

        return currentChecksum == latestChecksum
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
        if (file.exists()) {
            file.delete()
            file.createNewFile()
        }

        // Redownload the mod
        ModInstaller.downloadFile(entry.url, "$modsFolder/$id.jar")
    }
}