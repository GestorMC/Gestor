package com.gestormc.gestor.task

import com.gestormc.gestor.data.ReleaseEntry
import java.io.File

/**
 * A [Task] for handling mod dependency updating
 */
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
                    if (checkUpdates(cacheFolderPath, entry, depPath)) {
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
