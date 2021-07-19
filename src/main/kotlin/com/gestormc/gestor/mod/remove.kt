package com.gestormc.gestor.mod

import java.io.File

fun fullRemoveTask(
    /**
     * The path to the mods folder
     */
    modsFolder: String,
    /**
     * The name of the main JAR file
     */
    mainJarName: String,
    /**
     * The list of names of dependency JAR files for each dependency
     */
    dependencyJarNames: List<String>) {

    val mainFile = File("$modsFolder/$mainJarName.jar")
    if (!mainFile.exists()) return
    mainFile.deleteRecursively()

    dependencyJarNames.forEach { jarName ->
        val depFile = File("$modsFolder/$jarName.jar")
        if (!depFile.exists()) return@forEach
        depFile.deleteRecursively()
    }
}
