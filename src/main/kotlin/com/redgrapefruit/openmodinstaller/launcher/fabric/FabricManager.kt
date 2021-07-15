package com.redgrapefruit.openmodinstaller.launcher.fabric

import com.redgrapefruit.openmodinstaller.task.downloadFile
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object FabricManager {
    /**
     * The URL to the manifest with versions of the Fabric Installer
     */
    private const val INSTALLER_MANIFEST_URL = "https://meta.fabricmc.net/v2/versions/installer"

    /**
     * The URL to the FabricMC Maven hosting
     */
    private const val FABRIC_MAVEN_URL = "https://maven.fabricmc.net"

    /**
     * Downloads the JAR for Fabric Installer
     */
    fun setupFabricInstaller(
        /**
         * The root directory for Minecraft since FabricMC-related files are stored there, not in dedicated cache by design
         */
        gamePath: String) {

        // Download the manifest first if it does not exist
        val manifestDirectoryFile = File("$gamePath/system/manifests")
        if (!manifestDirectoryFile.exists()) manifestDirectoryFile.mkdirs()

        val manifestFile = File("$gamePath/system/manifests/installer_manifest")
        if (!manifestFile.exists()) {
            downloadFile(INSTALLER_MANIFEST_URL, manifestFile.absolutePath)
        }

        // Read the manifest
        val manifestObject: JsonObject
        FileInputStream(manifestFile).use { stream ->
            manifestObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }

        // Get the latest manifest entry and obtain the version number
        val latestObject = manifestObject[manifestObject.keys.first()]!!.jsonObject
        val versionNumber = latestObject["version"]!!.jsonPrimitive.content

        // Construct a link with the JAR from FabricMC Maven and download the JAR
        val link = "$FABRIC_MAVEN_URL/net/fabricmc/fabric-installer/$versionNumber/fabric-installer-$versionNumber.jar"
        downloadFile(link, "$gamePath/system/fabric/installer/installer_$versionNumber.jar")

        // Also create a helper file that contains the installer version
        val versionFile = File("$gamePath/system/fabric/util/installer_version.txt")
        if (!versionFile.exists()) versionFile.createNewFile()
        FileOutputStream(versionFile).use { stream ->
            stream.write(versionNumber.encodeToByteArray())
        }
    }

    /**
     * Runs the Fabric Installer after its installation.
     *
     * If the installation hasn't occurred yet, it is run automatically by this function.
     */
    fun runFabricInstaller(
        /**
         * The root path for the game
         */
        gamePath: String,
        /**
         * The launched Minecraft version
         */
        targetVersion: String
    ) {
        // Get the helper file with the version
        val versionFile = File("$gamePath/system/fabric/util/installer")
        if (!versionFile.exists()) setupFabricInstaller(gamePath)
        val versionNumber: String
        FileInputStream(versionFile).use { stream ->
            versionNumber = stream.readBytes().decodeToString()
        }

        // Get the installer file
        val jarFile = File("$gamePath/system/fabric/installer/installer_$versionNumber.jar")
        if (!jarFile.exists()) setupFabricInstaller(gamePath)

        // Launch the installer file as in CLI
        val command = "java -jar ${jarFile.absolutePath} client -dir $gamePath -mcversion $targetVersion -noprofile -snapshot"
        val process = Runtime.getRuntime().exec(command) // Output observation like with the Minecraft process isn't enabled since it's rarely useful
        process.waitFor()
    }
}
