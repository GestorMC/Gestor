package com.gestormc.gestor.mod

import com.gestormc.gestor.data.Mod
import com.gestormc.gestor.data.ReleaseEntry
import com.gestormc.gestor.data.ReleaseType
import com.gestormc.gestor.util.downloadFile
import java.io.File

fun modInstallTask(
    /**
     * The target mods folder
     */
    modsFolder: String,
    /**
     * The [ReleaseEntry] for the mod to use when installing
     */
    entry: ReleaseEntry,
    /**
     * The name of the outputted JAR
     */
    jarName: String) {

    val path = "$modsFolder/$jarName.jar"
    if (File(path).exists()) {
        try {
            downloadFile(entry.url, path)
        } catch (exception: Exception) {
            // TODO: Handle with a popup (lucsoft)
        }
    }
}

fun modDependencyInstallTask(
    /**
     * The target mods folder
     */
    modsFolder: String,
    /**
     * The targeted [ReleaseType]s for **each** dependency
     */
    releaseTypes: List<ReleaseType>,
    /**
     * The names for **each** dependency JAR file
     */
    jarNames: List<String>,
    /**
     * The parsed [Mod] instance
     */
    mod: Mod) {

    // Check if the lists provided are invalid
    if (releaseTypes.size != jarNames.size && jarNames.size != mod.dependencies.size) {
        // TODO: Handle with a popup
        return
    }
    // Iterate and install each dependency
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
