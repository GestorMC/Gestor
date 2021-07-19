package com.gestormc.gestor.mod

import com.gestormc.gestor.data.ReleaseEntry
import com.gestormc.gestor.task.downloadFile
import com.gestormc.gestor.util.Hash
import java.io.File
import kotlin.random.Random

fun modUpdateTask(
    /**
     * The [ReleaseEntry] of the mod
     */
    entry: ReleaseEntry,
    /**
     * The name of the JAR
     */
    jarName: String,
    /**
     * The path to mods folder
     */
    modsFolder: String) {

    if (!checkUpdates(entry, "$modsFolder/$jarName.jar")) return

    val file = File("$modsFolder/$jarName.jar")
    if (file.exists()) file.deleteRecursively()
    downloadFile(entry.url, file.absolutePath)
}

fun modDependencyUpdateTask(
    /**
     * List of [ReleaseEntry]s for each dependency
     */
    entries: List<ReleaseEntry>,
    /**
     * List of names for the already downloaded dependencies
     */
    names: List<String>,
    /**
     * The path to mods folder
     */
    modsFolder: String) {

    // Check all dependencies for updates, update if the result of the check is true
    entries.forEach { entry ->
        names.forEach { name ->
            if (checkUpdates(entry, "$modsFolder/$name.jar")) {
                val file = File("$modsFolder/$name.jar")
                if (file.exists()) file.deleteRecursively()
                downloadFile(entry.url, file.absolutePath)
            }
        }
    }
}

/**
 * Checks for updates
 */
private fun checkUpdates(entry: ReleaseEntry, jarPath: String): Boolean {
    if (!File(jarPath).exists()) return false // extra check just in case

    // Download latest JAR file
    val latestJarPath = "./cache/dedicated/updater_${Random.nextInt(Int.MAX_VALUE)}"

    File(latestJarPath).createNewFile()
    downloadFile(entry.url, latestJarPath)

    // Compare checksums
    val currentChecksum = Hash.checksum(jarPath).decodeToString()
    val latestChecksum = Hash.checksum(latestJarPath).decodeToString()

    return currentChecksum == latestChecksum
}
