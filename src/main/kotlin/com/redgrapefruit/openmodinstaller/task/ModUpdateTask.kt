package com.redgrapefruit.openmodinstaller.task

import com.redgrapefruit.openmodinstaller.data.mod.ReleaseEntry
import com.redgrapefruit.openmodinstaller.util.CodecManager
import com.redgrapefruit.openmodinstaller.util.Hash
import java.io.File
import kotlin.random.Random

/**
 * The [Task] for updating the main mod
 */
object ModUpdateTask : Task<ModUpdatePreLaunchContext, ModUpdateLaunchContext, DefaultPostLaunchTaskContext> {
    /**
     * [BlockingValue] for if the mod has updates.
     */
    @BlockingValue
    private var hasUpdates: Boolean = false

    override fun preLaunch(context: ModUpdatePreLaunchContext) {
        context.apply { hasUpdates = checkUpdates(cacheFolderPath, entry, jarPath) }
    }

    override fun launch(context: ModUpdateLaunchContext) {
        if (!hasUpdates) return

        context.apply {
            val file = File("$modsFolder/$jarName.jar")

            if (file.exists()) file.delete()

            downloadFile(entry.url, file.absolutePath, createFile = true)
        }
    }

    override fun postLaunch(context: DefaultPostLaunchTaskContext) {
        hasUpdates = false
    }
}

/**
 * Checks for updates
 */
fun checkUpdates(cacheFolderPath: String, entry: ReleaseEntry, jarPath: String): Boolean {
    val latestJarPath: String

    // Check codec for latest JAR
    if (CodecManager.hasEntry(cacheFolderPath, entry.url)) {
        // Retrieve cached
        val index = CodecManager.getEntry(cacheFolderPath, entry.url)
        latestJarPath = "$cacheFolderPath/cache_$index"
    } else {
        // Download and add to codec
        val index = Random.nextInt(Int.MAX_VALUE).toString()
        latestJarPath = "$cacheFolderPath/cache_$index"
        CodecManager.addEntry(cacheFolderPath, index, entry.url)

        File(latestJarPath).createNewFile()
        downloadFile(entry.url, latestJarPath)
    }

    // Compare checksums
    val currentChecksum = Hash.checksum(jarPath).decodeToString()
    val latestChecksum = Hash.checksum(latestJarPath).decodeToString()

    return currentChecksum == latestChecksum
}

data class ModUpdatePreLaunchContext(
    /**
     * The [ReleaseEntry] of the mod
     */
    val entry: ReleaseEntry,
    /**
     * The path to the already downloaded JAR
     */
    val jarPath: String,
    /**
     * The path to dedicated cache folder **with** a cache codec
     */
    val cacheFolderPath: String
) : PreLaunchTaskContext


data class ModUpdateLaunchContext(
    /**
     * The [ReleaseEntry] of the mod
     */
    val entry: ReleaseEntry,
    /**
     * The name of the JAR
     */
    val jarName: String,
    /**
     * The path to mods folder
     */
    val modsFolder: String
) : LaunchTaskContext
