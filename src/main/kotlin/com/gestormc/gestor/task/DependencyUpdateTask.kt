package com.gestormc.gestor.task

import com.gestormc.gestor.data.ReleaseEntry
import com.gestormc.gestor.util.Hash
import java.io.File
import kotlin.random.Random

/**
 * A [Task] for handling mod dependency updating
 */
@Deprecated("Built on the old Task API. Currently being migrated")
object DependencyUpdateTask :
    Task<DependencyUpdatePreLaunchContext, DependencyUpdateLaunchContext, DefaultPostLaunchTaskContext> {
    /**
     * [BlockingValue] for when **any** dependency has updates.
     *
     * Can only be `false` if **all** dependencies are up-to-date
     */
    @BlockingValue
    private var hasUpdates: Boolean = false

    /**
     * List of all updatable dependency JAR paths.
     *
     * Empty if [hasUpdates] is `false`.
     */
    private val updatables: MutableMap<ReleaseEntry, String> = mutableMapOf()

    override fun preLaunch(context: DependencyUpdatePreLaunchContext) {
        context.apply {
            // Check all dependencies for updates
            entries.forEach { entry ->
                paths.forEach { depPath ->
                    if (checkUpdates(entry, depPath)) {
                        hasUpdates = true
                        updatables[entry] = depPath
                    }
                }
            }
        }
    }

    override fun launch(context: DependencyUpdateLaunchContext) {
        if (!hasUpdates) return

        context.apply {
            updatables.forEach { (entry, depPath) ->
                val file = File(depPath)

                if (file.exists()) file.delete()

                downloadFile(entry.url, depPath)
            }
        }
    }

    override fun postLaunch(context: DefaultPostLaunchTaskContext) {
        hasUpdates = false
        updatables.clear()
    }
}

@Deprecated("Copied from com.gestormc.gestor.mod.UpdateKt for compatibility until the old tasks get removed")
internal fun checkUpdates(entry: ReleaseEntry, jarPath: String): Boolean {
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

data class DependencyUpdatePreLaunchContext(
    /**
     * List of [ReleaseEntry]s for each dependency
     */
    val entries: List<ReleaseEntry>,
    /**
     * List of absolute paths to the already downloaded JARs for dependencies
     */
    val paths: List<String>,
    /**
     * The path to the cache folder
     */
    val cacheFolderPath: String
) : PreLaunchTaskContext

data class DependencyUpdateLaunchContext(
    /**
     * List of [ReleaseEntry]s for each dependency
     */
    val entries: List<ReleaseEntry>,
    /**
     * List of absolute paths to the already downloaded JARs for dependencies
     */
    val paths: List<String>,
    /**
     * The path to the mods folder
     */
    val modsFolder: String
) : LaunchTaskContext
