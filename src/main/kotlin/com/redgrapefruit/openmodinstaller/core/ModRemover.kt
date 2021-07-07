package com.redgrapefruit.openmodinstaller.core

import com.redgrapefruit.openmodinstaller.data.mod.ReleaseEntry
import java.io.File

/**
 * Removes your mods
 */
@Deprecated("This system is deprecated. Currently being migrated to the Task architecture")
object ModRemover {
    /**
     * Removes the given mod
     */
    fun remove(
        /**
         * The target [ReleaseEntry]
         */
        entry: ReleaseEntry,
        /**
         * The mods folder
         */
        modsFolder: String,
        /**
         * The mod ID of the mod
         */
        id: String
    ) {
        val file = File("$modsFolder/$id.jar")

        if (!file.exists()) return

        file.delete()
    }
}
