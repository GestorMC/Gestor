package com.redgrapefruit.openmodinstaller.util

import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile

// Utils for Un-JAR-ing Minecraft mod JARs

/**
 * Un-jars the [input] JAR to the [output] directory
 */
fun unjar(input: String, output: String) {
    // Create  a JarFile instance and iterate through each entry
    val jarfile = JarFile(input)
    val entries = jarfile.entries()

    while (entries.hasMoreElements()) {
        // Get the entry and the File
        val entry = entries.nextElement()

        var file = File(output, entry.name)

        // Check if the target is a file or a directory
        if (!file.exists()) {
            // Make all the parent directories
            file.parentFile.mkdirs()
            // Magic
            file = File(output, entry.name)
        }
        if (file.isDirectory) {
            continue
        }

        // Read-write
        jarfile.getInputStream(entry).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                outputStream.write(inputStream.readBytes())
            }
        }
    }
}