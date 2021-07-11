package com.redgrapefruit.openmodinstaller.task

import com.redgrapefruit.openmodinstaller.data.Mod
import com.redgrapefruit.openmodinstaller.data.ReleaseType
import java.io.File

/**
 * A [Task] for automatic dependency installation.
 *
 * Can be cancelled if dependencies are already in place.
 */
object DependencyInstallTask :
    Task<DefaultPreLaunchTaskContext, DependencyInstallLaunchContext, DefaultPostLaunchTaskContext> {
    override fun launch(context: DependencyInstallLaunchContext) {
        context.apply {
            if (releaseTypes.size != jarNames.size && jarNames.size != mod.dependencies.size) {
                // TODO: Handle with a popup RenderTask (lucsoft)
                return
            }
            // This is a "solution"
            releaseTypes.forEach { type ->
                jarNames.forEach { jarName ->
                    mod.dependencies.forEach { wrapper ->
                        val entry = type.getEntry(false, mod, wrapper.id)

                        val path = "$modsFolder/$jarName.jar"
                        val file = File(path)

                        // Avoid rewrites completely
                        if (file.exists()) file.delete()

                        downloadFile(entry.url, path, true)
                    }
                }
            }
        }
    }
}

/**
 * The [LaunchTaskContext] for the [DependencyInstallTask]
 */
data class DependencyInstallLaunchContext(
    /**
     * The target mods folder
     */
    val modsFolder: String,
    /**
     * The targeted [ReleaseType]s for **each** dependency
     */
    val releaseTypes: List<ReleaseType>,
    /**
     * The names for **each** dependency JAR file
     */
    val jarNames: List<String>,
    /**
     * The parsed [Mod] instance
     */
    val mod: Mod
) : LaunchTaskContext
