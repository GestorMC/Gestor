package com.redgrapefruit.openmodinstaller.launcher.fabric

import com.redgrapefruit.openmodinstaller.JSON
import com.redgrapefruit.openmodinstaller.launcher.OpenLauncher
import com.redgrapefruit.openmodinstaller.task.downloadFile
import kotlinx.serialization.json.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Manages creating environments plausible for launching Fabric Minecraft.
 */
object FabricManager {
    /**
     * The URL for a manifest on meta.fabricmc.net that contains all Fabric Installer versions and their download links
     */
    private const val INSTALLER_MANIFEST_URL = "https://meta.fabricmc.net/v2/versions/installer"

    /**
     * Sets up the Fabric Installer
     */
    fun setupInstaller(
        /**
         * The root for the Minecraft directory
         */
        gamePath: String) {

        // Download the manifest with installer versions
        val manifestFolderFile = File("$gamePath/openmodinstaller/fabric/manifests/")
        if (!manifestFolderFile.exists()) manifestFolderFile.mkdirs()
        val manifestFile = File("${manifestFolderFile.absolutePath}/installer_manifest")
        if (!manifestFile.exists()) downloadFile(INSTALLER_MANIFEST_URL, manifestFile.absolutePath)

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

    /**
     * Runs the Fabric Installer and copies over the version info to the correct directory used by the launcher
     */
    fun runInstaller(
        /**
         * The root for the Minecraft directory
         */
        gamePath: String,
        /**
         * The targeted Minecraft version
         */
        targetVersion: String,
        /**
         * Use legacy Java 8 for older Minecraft versions (1.16 and lower)
         */
        optInLegacyJava: Boolean = false) {

        // Parse the manifest JSON again in order to find out the installer version
        val manifestFile = File("$gamePath/openmodinstaller/fabric/manifests/installer_manifest")
        if (!manifestFile.exists()) setupInstaller(gamePath) // extra bit of safety here

        val manifestObject: JsonObject
        FileInputStream(manifestFile).use { stream ->
            manifestObject = JSON.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }

        // Locate the installer
        val installerVersion = manifestObject["array"]!!.jsonArray[0].jsonObject["url"]!!.jsonPrimitive.content
        val installerPath = "$gamePath/openmodinstaller/fabric/installer/installer_$installerVersion.jar"

        // Create a command for launching the installer and launch it without output observation
        val command = "${OpenLauncher.findLocalJavaPath(optInLegacyJava)} -jar $installerPath client -dir $gamePath -mcversion $targetVersion -snapshot -noprofile"
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
}
