package com.redgrapefruit.openmodinstaller.util

import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.io.IOUtils
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Paths

// Apache Commons Compress abstractions

private val ARCHIVE_STREAM_FACTORY = ArchiveStreamFactory()

/**
 * Un-jars the [input] JAR to the [output] directory
 */
fun unjar(input: String, output: String) = unarchive<JarArchiveInputStream>(input, output, ArchiveStreamFactory.JAR)

/**
 * Un-zips the [input] ZIP to the [output] directory
 */
fun unzip(input: String, output: String) = unarchive<ZipArchiveInputStream>(input, output, ArchiveStreamFactory.ZIP)

/**
 * Un-TARs the [input] TAR.GZ to the [output] directory
 */
fun untar(input: String, output: String) = unarchive<TarArchiveInputStream>(input, output, ArchiveStreamFactory.TAR)

/**
 * Un-archives some type of an archive since Compress uses a more-or-less unified API for all of the archive types
 */
private inline fun <reified TStreamType : ArchiveInputStream> unarchive(
    /**
     * URI path to the archive file
     */
    input: String,
    /**
     * URI path to the output directory
     */
    output: String,
    /**
     * A factory for archive input streams which vary depending on the archive type
     */
    factory: String) {
    // Construct an InputStream
    val inputStream =  ARCHIVE_STREAM_FACTORY
        .createArchiveInputStream(factory, FileInputStream(input)) as TStreamType

    while (true) {
        // Iterate through entries
        val entry = inputStream.nextEntry ?: break

        if (!inputStream.canReadEntryData(entry)) {
            // TODO: Display a popup notifying the user that the app couldn't extract the contents of an archive (lucsoft)
            continue
        }

        val path = Paths.get(output, entry.name)
        val file = path.toFile()

        if (entry.isDirectory && !file.isDirectory) {
            file.mkdirs()
        } else {
            val parentFile = file.parentFile
            if (!parentFile.isDirectory) parentFile.mkdirs()

            FileOutputStream(parentFile).use { stream ->
                IOUtils.copy(inputStream, stream)
            }
        }
    }
}