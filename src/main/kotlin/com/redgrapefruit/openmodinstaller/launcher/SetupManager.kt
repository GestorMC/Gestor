package com.redgrapefruit.openmodinstaller.launcher

import com.redgrapefruit.openmodinstaller.data.ManifestReleaseEntry
import com.redgrapefruit.openmodinstaller.data.VersionManifest
import com.redgrapefruit.openmodinstaller.task.downloadFile
import kotlinx.serialization.json.*
import java.io.File
import java.io.FileInputStream
import kotlin.random.Random

/**
 * Manages the code required to setup a plausible environment for running Minecraft.
 */
object SetupManager {
    private const val MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json"

    /**
     * Sets up the necessary `${VERSION}.info` file.
     *
     * Will download the file if necessary
     */
    fun setupVersionInfo(
        /**
         * A local URI to the game's root folder
         */
        gamePath: String,
        /**
         * The ID of the target version. For example, `"1.17"`
         */
        targetVersion: String
        ) {
        var game = File(gamePath)

        if (!game.exists()) game.mkdirs()

        // Check if the version info exists first
        val versionInfoPath = "$gamePath/versions/$targetVersion/$targetVersion.json"
        val versionInfoFile = File(versionInfoPath)

        if (versionInfoFile.exists()) return

        // Download the manifest
        val manifestPath = "./cache/dedicated/manifest_${Random.nextInt(Int.MAX_VALUE)}"
        downloadFile(MANIFEST_URL, manifestPath)

        val manifest: VersionManifest
        FileInputStream(manifestPath).use { stream ->
            manifest = Json.decodeFromString(VersionManifest.serializer(), stream.readBytes().decodeToString())
        }

        // Iterate the versions from the manifest to see the ManifestReleaseEntry for the needed release
        var entry: ManifestReleaseEntry? = null
        manifest.versions.forEach { testEntry ->
            if (testEntry.id == targetVersion) entry = testEntry
        }
        // Check if there's no such version
        if (entry == null) throw RuntimeException("There's no such Minecraft version as $targetVersion")

        // Download the version info
        downloadFile(entry!!.url, versionInfoPath)
    }

    /**
     * Sets up the game JAR file to execute.
     *
     * For this launcher, server Minecraft is not supported.
     */
    fun setupJAR(
        /**
         * A local URI to the game's root folder
         */
        gamePath: String,
        /**
         * The targeted Minecraft version
         */
        targetVersion: String) {

        val versionInfoPath = "$gamePath/versions/$targetVersion/$targetVersion.json"

        // Read the version info JSON
        val versionInfoFile = File(versionInfoPath)

        val versionInfoObject: JsonObject
        FileInputStream(versionInfoFile).use { stream ->
            versionInfoObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }

        // Get the URL for the JAR
        val jarURL =
            // /downloads
            versionInfoObject["downloads"]!!
            // /downloads/client
            .jsonObject["client"]!!
            // /downloads/client/url
            .jsonObject["url"]!!.jsonPrimitive.content

        // Search for the JAR in the appropriate codec, if has entry, copy it to the target folder, else download it
        val jarPath = "$gamePath/versions/$targetVersion/$targetVersion.jar"

        // This is a quite heavy process and always takes a while if not in OkHttp cache
        downloadFile(jarURL, jarPath)
    }

    /**
     * Sets up Minecraft libraries, downloads missing and unzips natives
     */
    fun setupLibraries(
        /**
         * The URI to the game's root directory
         */
        gamePath: String,
        /**
         * The targeted Minecraft version for this launch
         */
        targetVersion: String) {

        val versionInfoPath = "$gamePath/versions/$targetVersion/$targetVersion.json"

        // Load version info JsonObject
        val versionInfoObject: JsonObject
        FileInputStream(versionInfoPath).use { stream ->
            versionInfoObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString()).jsonObject
        }

        // Determine native libraries path and clear it beforehand
        val nativesFile = File("$gamePath/versions/$targetVersion/$targetVersion-Natives/")
        nativesFile.deleteRecursively()
        nativesFile.mkdirs()

        // Get libraries JsonArray from the JsonObject
        val librariesArray = versionInfoObject["libraries"]!!.jsonArray

        // Launch LibraryManager
        LibraryManager.checkLibraries(gamePath, versionInfoObject, librariesArray, nativesFile.absolutePath)
    }

    /**
     * Sets up a local Java install, not reusable anywhere outside of the launcher.
     *
     * If [targetVersion] is below 1.17, JRE 8 is installed.
     *
     * If [targetVersion] is 1.17 or higher, JDK 16 is installed.
     */
    fun setupJava(
        /**
         * The targeted Minecraft version
         */
        targetVersion: String) {


    }
}
