package com.gestormc.gestor.launcher.fabric

import com.gestormc.gestor.JSON
import com.gestormc.gestor.launcher.GestorLauncher
import com.gestormc.gestor.task.downloadFile
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * The URL for a manifest on meta.fabricmc.net that contains all Fabric Installer versions and their download links
 */
private const val INSTALLER_MANIFEST_URL = "https://meta.fabricmc.net/v2/versions/installer"

fun fabricSetupInstallerTask(
    /**
     * The path to the root game directory
     */
    gamePath: String) {

    // Download the manifest with installer versions
    val manifestFolderFile = File("$gamePath/openmodinstaller/fabric/manifests/")
    if (!manifestFolderFile.exists()) manifestFolderFile.mkdirs()
    val manifestFile = File("${manifestFolderFile.absolutePath}/installer_manifest")
    if (!manifestFile.exists()) downloadFile(INSTALLER_MANIFEST_URL, manifestFile.absolutePath)

    val initial: String
    FileInputStream(manifestFile).use { stream ->
        initial = stream.readBytes().decodeToString()
    }

    if (!initial.contains("{\n\t\"array\": ")) {
        FileOutputStream(manifestFile).use { stream ->
            // Hotfix at runtime because the formatting on the manifests is bad
            stream.write("{\n\t\"array\": $initial}".encodeToByteArray())
        }
    }

    // Parse the manifest JSON
    val manifestObject: JsonObject
    FileInputStream(manifestFile).use { stream ->
        manifestObject = JSON.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
    }

    // Get the URL to the latest installer JAR and download it
    val installerURL = manifestObject["array"]!!.jsonArray[0].jsonObject["url"]!!.jsonPrimitive.content
    val installerVersion = manifestObject["array"]!!.jsonArray[0].jsonObject["version"]!!.jsonPrimitive.content
    downloadFile(installerURL, "$gamePath/openmodinstaller/fabric/installer/installer_$installerVersion.jar")
}

fun fabricRunInstallerTask(
    /**
     * The path to the root directory of the game
     */
    gamePath: String,
    /**
     * The launched Minecraft version
     */
    targetVersion: String,
    /**
     * Opt in legacy AdoptOpenJRE 8 for 1.16 and lower versions of Minecraft
     */
    optInLegacyJava: Boolean) {

    // Parse the manifest JSON again in order to find out the installer version
    val manifestFile = File("$gamePath/openmodinstaller/fabric/manifests/installer_manifest")
    if (!manifestFile.exists()) fabricSetupInstallerTask(gamePath) // extra bit of safety here

    val manifestObject: JsonObject
    FileInputStream(manifestFile).use { stream ->
        manifestObject = JSON.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
    }

    // Locate the installer
    val installerVersion = manifestObject["array"]!!.jsonArray[0].jsonObject["version"]!!.jsonPrimitive.content
    val installerPath = "$gamePath/openmodinstaller/fabric/installer/installer_$installerVersion.jar"

    // Create a command for launching the installer and launch it without output observation
    val command =
        "${GestorLauncher.findLocalJavaPath(optInLegacyJava)} -jar $installerPath client -dir $gamePath -mcversion $targetVersion -snapshot -noprofile"
    println(command)
    val process = Runtime.getRuntime().exec(command)
    process.waitFor()

    // Locate the directory that the installer has put our version info into
    val versionsFolderFile = File("$gamePath/versions")
    var sourceFolderPath: String? = null
    versionsFolderFile.listFiles()!!.forEach { file ->
        if (file.isDirectory) {
            val path = file.absolutePath
            if (path.contains(targetVersion) && path.contains("fabric")) {
                sourceFolderPath = path
            }
        }
    }
    if (sourceFolderPath == null) throw RuntimeException("Could not locate the directory that the Fabric Installer has installed the version info into")

    // Locate the version info
    var versionInfoPath: String? = null
    File(sourceFolderPath!!).listFiles()!!.forEach { file ->
        if (file.absolutePath.endsWith(".json")) {
            versionInfoPath = file.absolutePath
        }
    }
    if (versionInfoPath == null) throw RuntimeException("Could not locate the version info created by the Fabric Installer")

    // Move the version info
    val newVersionInfoFolderFile = File("$gamePath/versions/$targetVersion-fabric/")
    if (!newVersionInfoFolderFile.exists()) newVersionInfoFolderFile.mkdirs()
    val newVersionInfoPath = "${newVersionInfoFolderFile.absolutePath}/$targetVersion-fabric.json"
    Files.copy(Paths.get(versionInfoPath!!), FileOutputStream(newVersionInfoPath))
    // Remove the entire old directory
    File(sourceFolderPath!!).deleteRecursively()
}

fun fabricMigrateJarTask(
    /**
     * The path to the root directory of the game
     */
    gamePath: String,
    /**
     * The launched Minecraft version
     */
    targetVersion: String) {

    val oldPath = "$gamePath/versions/$targetVersion/$targetVersion-client.jar"
    val newPath = "$gamePath/versions/$targetVersion-fabric/$targetVersion-fabric.jar"

    Files.copy(Paths.get(oldPath), FileOutputStream(newPath))
}
