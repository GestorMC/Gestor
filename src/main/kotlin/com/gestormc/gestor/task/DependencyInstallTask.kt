package com.gestormc.gestor.task

import com.gestormc.gestor.data.Mod
import com.gestormc.gestor.data.ReleaseType
import java.io.File

/**
 * A [Task] for automatic dependency installation.
 *
 * Can be cancelled if dependencies are already in place.
 */
@Deprecated("Built on the old Task API. Currently being migrated")
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

                        downloadFile(entry.url, path)
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
