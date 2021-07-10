package com.redgrapefruit.openmodinstaller.core

import com.redgrapefruit.openmodinstaller.data.Mod
import com.redgrapefruit.openmodinstaller.data.ReleaseEntry
import com.redgrapefruit.openmodinstaller.data.ReleaseType
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Installs mods from the [DistributionSource]
 */
@Deprecated("Use ModInstallTask and DependencyInstallTask")
object ModInstaller {
    private const val BUFFER_SIZE = 512000

    /**
     * Downloads a file from the Internet using the built-in `java.nio` API
     */
    fun downloadFile(
        /**
         * The input URL, remote
         */
        input: String,
        /**
         * The output path, local
         */
        output: String
    ) {
        val httpConn = URL(input).openConnection() as HttpURLConnection
        httpConn.requestMethod = "GET"

        httpConn.inputStream.use { inputStream ->
            FileOutputStream(output).use { outputStream ->
                var bytesRead: Int
                val buffer = ByteArray(BUFFER_SIZE)

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        }
    }

    /**
     * A convenience method applying [downloadFile] onto a real environment
     */
    private fun downloadMod(
        /**
         * Input URL, remote
         */
        input: String,
        /**
         * Output path, local
         */
        output: String
    ) {
        // Init the File with the output path and (re)create it
        val outputFile = File(output)
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        } else {
            outputFile.delete()
            outputFile.createNewFile()
        }
        // Call the low-level downloadFile
        downloadFile(input, output)
    }

    /**
     * Downloads a mod via the given parameters
     */
    fun downloadFromJSON(
        /**
         * The mods folder
         */
        modsFolder: String,
        /**
         * The linked [ReleaseEntry]
         */
        entry: ReleaseEntry,
        /**
         * Is the installed mod main or dependency
         */
        isMain: Boolean,
        /**
         * Main mod ID. Specified if [isMain] = true
         */
        mainId: String = "",
        /**
         * Dependency mod ID. Specified if [isMain] = false
         */
        depId: String = ""
    ) {
        downloadMod(entry.url, "$modsFolder/${if (isMain) mainId else depId}.jar")
    }

    /**
     * Downloads the mod **and** its dependencies
     */
    fun downloadAllFromJSON(
        modsFolder: String,
        mod: Mod,
        releaseType: ReleaseType
    ) {
        // Download main
        val mainEntry = releaseType.getEntry(isMain = true, mod)
        downloadFromJSON(modsFolder, mainEntry, isMain = true, mainId = mod.meta.name.lowercase())
        // Download dependencies (legacy)
//        mod.dependencies.forEach { wrapper ->
//            // TODO: Support different release types for dependencies
//            val depEntry: ReleaseEntry = if (wrapper.id == "croptopia") {
//                ReleaseType.Stable.getEntry(isMain = false, mod, wrapper.id)
//            } else {
//                ReleaseType.Snapshot.getEntry(isMain = false, mod, wrapper.id)
//            }
//            downloadFromJSON(modsFolder, depEntry, isMain = false, depId = wrapper.id)
//        }
    }
}
