package com.redgrapefruit.openmodinstaller.core

import com.redgrapefruit.openmodinstaller.data.distribution.DistributionSource
import com.redgrapefruit.openmodinstaller.data.mod.Mod
import com.redgrapefruit.openmodinstaller.data.mod.ReleaseType
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel


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
        var readableBC: ReadableByteChannel? = null
        var fileOS: FileOutputStream? = null
        try {
            val urlObj = URL(input)
            readableBC = Channels.newChannel(urlObj.openStream())
            fileOS = FileOutputStream(output)
            fileOS.channel.transferFrom(readableBC, 0, Long.MAX_VALUE)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fileOS?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                readableBC?.close()
            } catch (e: IOException) {
                e.printStackTrace()
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
