package com.gestormc.gestor.task

import com.gestormc.gestor.data.ReleaseEntry
import com.gestormc.gestor.util.Hash
import java.io.File
import kotlin.random.Random

/**
 * The [Task] for updating the main mod
 */
@Deprecated("Built on the old Task API. Currently being migrated")
object ModUpdateTask : Task<ModUpdatePreLaunchContext, ModUpdateLaunchContext, DefaultPostLaunchTaskContext> {
    /**
     * [BlockingValue] for if the mod has updates.
     */
    @BlockingValue
    private var hasUpdates: Boolean = false

    override fun preLaunch(context: ModUpdatePreLaunchContext) {
        context.apply { hasUpdates = checkUpdates(entry, jarPath) }
    }

    override fun launch(context: ModUpdateLaunchContext) {
        if (!hasUpdates) return

        context.apply {
            val file = File("$modsFolder/$jarName.jar")

            if (file.exists()) file.delete()

            downloadFile(entry.url, file.absolutePath)
        }
    }

    override fun postLaunch(context: DefaultPostLaunchTaskContext) {
        hasUpdates = false
    }
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
