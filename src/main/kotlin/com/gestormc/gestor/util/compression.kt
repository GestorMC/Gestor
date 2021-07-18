package com.gestormc.gestor.util

import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveException
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.io.IOUtils
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
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
    val inputStream: InputStream?
    try {
        val filePath: Path = Paths.get(input)
        inputStream = Files.newInputStream(filePath)
        val archiveStreamFactory = ArchiveStreamFactory()
        val archiveInputStream = archiveStreamFactory.createArchiveInputStream(factory, inputStream) as TStreamType
        var archiveEntry: ArchiveEntry?
        while (archiveInputStream.nextEntry.also { archiveEntry = it } != null) {
            val path = Paths.get(output, archiveEntry!!.name)
            val file = path.toFile()
            if (archiveEntry!!.isDirectory) {
                if (!file.isDirectory) {
                    file.mkdirs()
                }
            } else {
                val parent = file.parentFile
                if (!parent.isDirectory) {
                    parent.mkdirs()
                }
                Files.newOutputStream(path).use { outputStream ->
                    IOUtils.copy(
                        archiveInputStream,
                        outputStream
                    )
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: ArchiveException) {
        e.printStackTrace()
    }
}