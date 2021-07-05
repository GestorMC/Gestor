package com.redgrapefruit.openmodinstaller.core

import com.redgrapefruit.openmodinstaller.data.distribution.DistributionSource
import com.redgrapefruit.openmodinstaller.data.mod.Mod
import com.redgrapefruit.openmodinstaller.data.mod.ReleaseType
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

private const val BUFFER_SIZE = 512000

/**
 * Installs mods from the [DistributionSource]
 */
object ModInstaller {
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
         * The serialized [Mod] structure
         */
        mod: Mod,
        /**
         * The target [ReleaseType]
         */
        releaseType: ReleaseType,
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
        val entry = releaseType.getEntry(isMain, mod, depId)

        downloadMod(entry.url, "$modsFolder/${if (isMain) mainId else depId}.jar")
    }
}
