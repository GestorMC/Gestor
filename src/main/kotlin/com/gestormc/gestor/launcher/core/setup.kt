package com.gestormc.gestor.launcher.core

import com.gestormc.gestor.data.ManifestReleaseEntry
import com.gestormc.gestor.data.VersionManifest
import com.gestormc.gestor.launcher.GestorLauncher
import com.gestormc.gestor.util.downloadFile
import com.gestormc.gestor.util.fullUntar
import com.gestormc.gestor.util.unzip
import kotlinx.serialization.json.*
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.random.Random

// Manifest with MC versions
private const val MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json"

// The sub-sub-domain of Minecraft.net containing asset downloads for the asset index
private const val ASSET_DOWNLOAD_DOMAIN_URL = "https://resources.download.minecraft.net"

// AdoptOpenJRE 8 downloads (legacy, opt-in for older versions)
private const val JRE_8_WINDOWS =
    "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u292-b10/OpenJDK8U-jre_x64_windows_hotspot_8u292b10.zip"
private const val JRE_8_LINUX =
    "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u292-b10/OpenJDK8U-jre_x64_linux_hotspot_8u292b10.tar.gz"
private const val JRE_8_OSX =
    "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u292-b10/OpenJDK8U-jre_x64_mac_hotspot_8u292b10.tar.gz"

// AdoptOpenJRE 16 downloads (default)
private const val JRE_16_WINDOWS =
    "https://github.com/AdoptOpenJDK/openjdk16-binaries/releases/download/jdk-16.0.1%2B9/OpenJDK16U-jre_x64_windows_hotspot_16.0.1_9.zip"
private const val JRE_16_LINUX =
    "https://github.com/AdoptOpenJDK/openjdk16-binaries/releases/download/jdk-16.0.1%2B9/OpenJDK16U-jre_x64_linux_hotspot_16.0.1_9.tar.gz"
private const val JRE_16_OSX =
    "https://github.com/AdoptOpenJDK/openjdk16-binaries/releases/download/jdk-16.0.1%2B9/OpenJDK16U-jre_x64_mac_hotspot_16.0.1_9.tar.gz"

fun launcherVersionInfoSetupTask(
    /**
     * A local URI to the game's root folder
     */
    gamePath: String,
    /**
     * The ID of the target version. For example, `"1.17"`
     */
    targetVersion: String) {

    val game = File(gamePath)

    if (!game.exists()) game.mkdirs()

    // Check if the version info exists first
    val versionInfoPath = "$gamePath/versions/$targetVersion/$targetVersion.json"
    val versionInfoFile = File(versionInfoPath)

    if (versionInfoFile.exists()) return

    // Download the manifest
    val manifestPath = "$gamePath/versions/version_manifest.json"
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

fun launcherSetupJarTask(
    /**
     * A local URI to the game's root folder
     */
    gamePath: String,
    /**
     * The targeted Minecraft version
     */
    targetVersion: String,
    /**
     * Download the Minecraft server JAR
     */
    downloadServer: Boolean = false) {

    val versionInfoPath = "$gamePath/versions/$targetVersion/$targetVersion.json"

    // Read the version info JSON
    val versionInfoFile = File(versionInfoPath)

    val versionInfoObject: JsonObject
    FileInputStream(versionInfoFile).use { stream ->
        versionInfoObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
    }

    // Get the URL for the JAR
    val jarTemplate = if (downloadServer) "server" else "client"

    val jarURL =
        if (versionInfoObject.contains("inheritsFrom") && !versionInfoObject.contains("downloads")) { // inheritance support
            // Get parent JAR URL
            GestorLauncher.getParentObject(versionInfoObject, gamePath)["downloads"]!!
                .jsonObject[jarTemplate]!!
                .jsonObject["url"]!!.jsonPrimitive.content

        } else {
            versionInfoObject["downloads"]!!
                .jsonObject[jarTemplate]!!
                .jsonObject["url"]!!.jsonPrimitive.content
        }

    // This is a quite heavy process and always takes a while if not in OkHttp cache
    val jarPath = "$gamePath/versions/$targetVersion/$targetVersion-$jarTemplate.jar"
    downloadFile(jarURL, jarPath)
}

fun launcherSetupLibrariesTask(
    /**
     * The URI to the game's root directory
     */
    gamePath: String,
    /**
     * The targeted Minecraft version for this launch
     */
    targetVersion: String,
    /**
     * The [LibraryData] for library checking
     */
    data: LibraryData) {

    val versionInfoPath = "$gamePath/versions/$targetVersion/$targetVersion.json"

    // Load version info JsonObject
    val versionInfoObject: JsonObject
    FileInputStream(versionInfoPath).use { stream ->
        versionInfoObject =
            Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString()).jsonObject
    }

    // Determine native libraries path and clear it beforehand
    val nativesFile = File("$gamePath/natives/")
    nativesFile.deleteRecursively()
    nativesFile.mkdirs()

    // Get libraries JsonArray from the JsonObject
    val librariesArray = versionInfoObject["libraries"]!!.jsonArray

    // Launch LibraryManager check first, then set up natives
    launcherCheckLibraryTask(gamePath, versionInfoObject, librariesArray, nativesFile.absolutePath, data)
}

fun launcherSetupJavaTask(
    /**
     * Use AdoptOpenJRE 8 legacy version instead of 16 for older versions
     */
    optInLegacyJava: Boolean = false) {

    // Check if Java is already installed
    val javaTargetPath = "./java/${if (optInLegacyJava) "adoptopenjre8" else "adoptopenjre16"}"
    val javaTargetFile = File(javaTargetPath)

    javaTargetFile.mkdirs()

    if (javaTargetFile.listFiles()!!.isNotEmpty()) return

    // We currently do not auto Java setup on Unix-based systems (Linux, OSX)
    if (SystemUtils.IS_OS_UNIX) {
        println("Warning: automatic Java setup is currently not supported on Unix-based systems")
        println("Ensure that you have Java on your system, if not, install it and add PATHs. Then enter anything in the console window to proceed")
        Scanner(System.`in`).next()
        return
    }

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
            SystemUtils.IS_OS_MAC_OSX -> JRE_16_OSX
            else -> throw RuntimeException("App not run on Windows, Linux or OSX")
        }
    }

    val useTarGZ = SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC_OSX

    // Download into dedicated cache
    val javaArchivePath = "./cache/dedicated/cache_${Random.nextInt(Int.MAX_VALUE)}.${if (SystemUtils.IS_OS_WINDOWS) "zip" else "tar.gz"}"
    File(javaArchivePath).mkdirs()
    downloadFile(javaURL, javaArchivePath)

    // UnZIP or UnTAR.GZ
    if (useTarGZ) {
        fullUntar(javaArchivePath, javaTargetPath)
    } else {
        unzip(javaArchivePath, javaTargetPath)
    }

    // On Unix, run chmod +x to grant execution permissions
    if (SystemUtils.IS_OS_UNIX) {
        // Find the bin folder
        var binFolder: String? = null
        val files = javaTargetFile.listFiles()!!
        if (files.size > 1) throw RuntimeException("Java directory overflow.")
        javaTargetFile.listFiles()!!.forEach { file ->
            binFolder = "${file.absolutePath}/bin/"
        }
        if (binFolder == null) throw RuntimeException("Could not locate Java bin folder.")

        val process = Runtime.getRuntime().exec("chmod +x *")
    }
}

fun launcherSetupAssetsTask(
    /**
     * The root folder of the game
     */
    gamePath: String,
    /**
     * The targeted Minecraft version
     */
    targetVersion: String) {

    // Ensure that the folder for asset indexes exists
    val assetIndexesFile = File("$gamePath/assets/indexes")
    if (!assetIndexesFile.exists()) assetIndexesFile.mkdirs()

    // Read the version info
    val versionInfoObject: JsonObject
    FileInputStream("$gamePath/versions/$targetVersion/$targetVersion.json").use { stream ->
        versionInfoObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
    }

    val indexVersion: String
    val url: String
    if (versionInfoObject.contains("inheritsFrom") && !versionInfoObject.contains("assets") && !versionInfoObject.contains(
            "assetIndex"
        )
    ) { // inheritance support
        val parentObject = GestorLauncher.getParentObject(versionInfoObject, gamePath)
        indexVersion = parentObject["assets"]!!.jsonPrimitive.content
        url = parentObject["assetIndex"]!!.jsonObject["url"]!!.jsonPrimitive.content
    } else {
        indexVersion = versionInfoObject["assets"]!!.jsonPrimitive.content
        url = versionInfoObject["assetIndex"]!!.jsonObject["url"]!!.jsonPrimitive.content
    }

    // Download the index
    downloadFile(input = url, output = "$assetIndexesFile/$indexVersion.json")

    // Read the asset index
    val assetIndexObject: JsonObject
    FileInputStream("$assetIndexesFile/$indexVersion.json").use { stream ->
        assetIndexObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
    }

    // Ensure the objects folder exists
    val objectsFile = File("$gamePath/assets/objects")
    if (objectsFile.exists()) objectsFile.mkdirs()

    // Iterate through all asset objects
    val objectsObject = assetIndexObject["objects"]!!.jsonObject // the naming is a bit cursed here though
    objectsObject.keys.forEach { key ->
        val assetObject = objectsObject[key]!!.jsonObject

        // Get hash and its first two symbols which act as a key in Minecraft's asset system
        val hash = assetObject["hash"]!!.jsonPrimitive.content
        val hashPrefix = hash.substring(0, 2)

        // Get the local path
        val localAssetPath = "${objectsFile.absolutePath}/$hashPrefix/$hash"
        val localAssetFile = File(localAssetPath)

        // Validate the local path, if doesn't exist, create the remote path and download from it
        if (!localAssetFile.exists()) {
            val remoteAssetPath = "$ASSET_DOWNLOAD_DOMAIN_URL/$hashPrefix/$hash"

            downloadFile(remoteAssetPath, localAssetPath)
        }
    }
}
