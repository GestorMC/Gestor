package com.redgrapefruit.openmodinstaller.task

import com.redgrapefruit.openmodinstaller.data.mod.ReleaseEntry
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels

/**
 * A [Task] which handles automatic installation of mods
 */
object ModInstallTask : Task<DefaultPreLaunchTaskContext, ModInstallLaunchContext, DefaultPostLaunchTaskContext> {
    override fun launch(context: ModInstallLaunchContext) {
        context.apply {
            downloadFile(entry.url, "$modsFolder/${jarName}.jar")
        }
    }
}

/**
 * Downloads a file from the Internet using the `java.nio` API
 */
fun downloadFile(
    /**
     * The input URL. Should be full (e.g. not `github.com`, but `https://www.github.com`)
     */
    input: String,
    /**
     * The output file URI
     */
    output: String,
    /**
     * If the file at the [output] URI doesn't exist, should the utility create it
     */
    createFile: Boolean = false) {

    // Create file if needed & requested
    if (createFile) {
        val outputFile = File(output)

        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }
    }

    // Make URL
    val url: URL = URL(input)

    // Open a channel for the URL and a stream for the output file
    val channel = Channels.newChannel(url.openStream())
    val stream = FileOutputStream(output)

    // Read from 0 to max (full file)
    stream.channel.transferFrom(channel, 0, Long.MAX_VALUE)

    // Close all streams and channels
    channel.close()
    stream.close()
}

/**
 * The [LaunchTaskContext] for [ModInstallTask]
 */
class ModInstallLaunchContext(
    /**
     * The target mods folder
     */
    val modsFolder: String,
    /**
     * The linked [ReleaseEntry]
     */
    val entry: ReleaseEntry,
    /**
     * The filename of the target JAR.
     *
     * If `mod`, the target file will be `mod.jar`
     */
    val jarName: String
) : LaunchTaskContext
