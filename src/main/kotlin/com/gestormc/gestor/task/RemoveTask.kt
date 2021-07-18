package com.gestormc.gestor.task

import com.gestormc.gestor.data.ReleaseEntry
import java.io.File

/**
 * A [Task] handling removal of the main mod **and** its dependencies in the same place for ease of use.
 */
object RemoveTask : Task<DefaultPreLaunchTaskContext, RemoveLaunchContext, DefaultPostLaunchTaskContext> {
    override fun launch(context: RemoveLaunchContext) {
        removeMain(context)
        removeDeps(context)
    }

    private fun removeMain(context: RemoveLaunchContext) {
        context.apply {
            val mainFile = File("$modsFolder/$mainJarName.jar")

            if (!mainFile.exists()) return

            mainFile.delete()
        }
    }

    private fun removeDeps(context: RemoveLaunchContext) {
        context.apply {
            depJarNames.forEach { jarName ->
                val depFile = File("$modsFolder/$jarName.jar")

                if (!depFile.exists()) return@forEach

                depFile.delete()
            }
        }
    }
}

data class RemoveLaunchContext(
    /**
     * Main mod [ReleaseEntry]
     */
    val mainEntry: ReleaseEntry,
    /**
     * The path to the mods folder
     */
    val modsFolder: String,
    /**
     * The name of the main JAR file
     */
    val mainJarName: String,
    /**
     * The list of names of dependency JAR files for each dependency
     */
    val depJarNames: List<String>
) : LaunchTaskContext
