package com.redgrapefruit.openmodinstaller.launcher

import com.redgrapefruit.openmodinstaller.data.ManifestReleaseEntry
import com.redgrapefruit.openmodinstaller.data.VersionManifest
import com.redgrapefruit.openmodinstaller.task.downloadFile
import com.redgrapefruit.openmodinstaller.util.untar
import com.redgrapefruit.openmodinstaller.util.unzip
import kotlinx.serialization.json.*
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.io.FileInputStream
import kotlin.random.Random

/**
 * Manages the code required to setup a plausible environment for running Minecraft.
 */
object SetupManager {
    // Manifest with MC versions
    private const val MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json"

    // AdoptOpenJRE 8 downloads (legacy, opt-in for older versions)
    private const val JRE_8_WINDOWS = "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u292-b10/OpenJDK8U-jre_x64_windows_hotspot_8u292b10.zip"
    private const val JRE_8_LINUX = "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u292-b10/OpenJDK8U-jre_x64_linux_hotspot_8u292b10.tar.gz"
    private const val JRE_8_OSX = "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u292-b10/OpenJDK8U-jre_x64_mac_hotspot_8u292b10.tar.gz"

    // AdoptOpenJRE 16 downloads (default)
    private const val JRE_16_WINDOWS = "https://github.com/AdoptOpenJDK/openjdk16-binaries/releases/download/jdk-16.0.1%2B9/OpenJDK16U-jre_x64_windows_hotspot_16.0.1_9.zip"
    private const val JRE_16_LINUX = "https://github.com/AdoptOpenJDK/openjdk16-binaries/releases/download/jdk-16.0.1%2B9/OpenJDK16U-jre_x64_linux_hotspot_16.0.1_9.tar.gz"
    private const val JRE_16_OSX = "https://github.com/AdoptOpenJDK/openjdk16-binaries/releases/download/jdk-16.0.1%2B9/OpenJDK16U-jre_x64_mac_hotspot_16.0.1_9.tar.gz"

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
        val game = File(gamePath)

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
        val nativesFile = File("$gamePath/natives/")
        nativesFile.deleteRecursively()
        nativesFile.mkdirs()

        // Get libraries JsonArray from the JsonObject
        val librariesArray = versionInfoObject["libraries"]!!.jsonArray

        // Launch LibraryManager
        LibraryManager.checkLibraries(gamePath, librariesArray, nativesFile.absolutePath)
    }

    /**
     * Sets up a local Java install, not reusable anywhere outside of the launcher.
     */
    fun setupJava(
        /**
         * Use AdoptOpenJRE 8 legacy version instead of 16 for older versions
         */
        optInLegacyJava: Boolean = false) {

        // Check if Java is already installed
        val javaTargetPath = "./java/${if (optInLegacyJava) "adoptopenjre8" else "adoptopenjre16"}"
        val javaTargetFile = File(javaTargetPath)

        javaTargetFile.mkdirs()

        if (javaTargetFile.listFiles()!!.isNotEmpty()) return

        val javaURL = if (optInLegacyJava) {
            when {
                SystemUtils.IS_OS_WINDOWS -> JRE_8_WINDOWS
                SystemUtils.IS_OS_LINUX -> JRE_8_LINUX
                SystemUtils.IS_OS_MAC_OSX -> JRE_8_OSX
                else -> throw RuntimeException("App not run on Windows, Linux or OSX")
            }
        } else {
            when {
                SystemUtils.IS_OS_WINDOWS -> JRE_16_WINDOWS
                SystemUtils.IS_OS_LINUX -> JRE_16_LINUX
                SystemUtils.IS_OS_MAC -> JRE_16_OSX
                else -> throw RuntimeException("App not run on Windows, Linux or OSX")
            }
        }

        val useTarGZ = SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC_OSX

        // Download into dedicated cache
        val javaArchivePath = "./cache/dedicated/cache_${Random.nextInt(Int.MAX_VALUE)}"
        File(javaArchivePath).mkdirs()
        downloadFile(javaURL, javaArchivePath)

        // UnZIP or UnTAR
        if (useTarGZ) {
            untar(javaArchivePath, javaTargetPath)
        } else {
            unzip(javaArchivePath, javaTargetPath)
        }
    }
}
