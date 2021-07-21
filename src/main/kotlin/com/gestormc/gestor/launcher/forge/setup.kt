package com.gestormc.gestor.launcher.forge

import com.gestormc.gestor.launcher.GestorLauncher
import com.gestormc.gestor.util.downloadFile
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * The URL to downloading the JAR of the latest ForgeHeadless release
 */
private const val HEADLESS_INSTALLER_URL =
    "https://github.com/xfl03/ForgeInstallerHeadless/releases/download/1.0.1/forge-installer-headless-1.0.1.jar"

fun forgeSetupInstallerTask(
    /**
     * The path to the root directory of the game
     */
    gamePath: String,
    /**
     * The launched Minecraft version
     */
    targetVersion: String) {

    // Check if the headless installer is already in-place
    val installerPath = "$gamePath/openmodinstaller/forge/installer/headless/forge-installer-headless-1.0.1.jar"
    if (!File(installerPath).exists()) {
        // Download the headless installer
        downloadFile(HEADLESS_INSTALLER_URL, installerPath)
    }

    // Download index
    val indexPath = "$gamePath/openmodinstaller/html/index/index_$targetVersion.html"
    if (!File(indexPath).exists()) {
        // Download the HTML index
        try {
            downloadFile(
                "https://files.minecraftforge.net/net/minecraftforge/forge/index_$targetVersion.html",
                indexPath
            )
        } catch (exception: Exception) {
            throw RuntimeException("Forge is currently not available for $targetVersion!")
        }
    }

    // Parse index using JSoup
    val document: Document
    FileInputStream(indexPath).use { stream ->
        document = Jsoup.parse(stream.readBytes().decodeToString())
    }
    // Get the boosted URL (adfoc.us)
    val boostedUrl = document.body()
        .getElementsByClass("sidebar-sticky-wrapper-content")[0]
        .getElementsByClass("promos-wrapper")[0]
        .getElementsByClass("promos-content")[0]
        .getElementsByClass("downloads")[0]
        .getElementsByClass("download")[1] // use recommended Forge, index 1
        .getElementsByClass("links")[0]
        .getElementsByClass("link-boosted")[0]
        .getElementsByTag("a")[0]
        .attr("href")
    // Remove the boost. No money Lex, no money for you
    val temp = boostedUrl.substring(boostedUrl.indexOf("&") + 1, boostedUrl.lastIndex + 1)
    val url = temp.substring(temp.indexOf("=") + 1, temp.lastIndex + 1)

    // Download main installer
    val mainInstallerPath =
        "$gamePath/openmodinstaller/forge/installer/official/forge-installer-official-$targetVersion.jar"
    if (!File(mainInstallerPath).exists()) {
        downloadFile(url, mainInstallerPath)
    }
}

fun forgeRunInstallerTask(
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

    // Check if the installer exists, if not, run the setup
    val installerPath = "$gamePath/openmodinstaller/forge/installer/headless/forge-installer-headless-1.0.1.jar"
    if (!File(installerPath).exists()) forgeSetupInstallerTask(gamePath, targetVersion)
    val mainInstallerPath =
        "$gamePath/openmodinstaller/forge/installer/official/forge-installer-official-$targetVersion.jar"
    if (!File(mainInstallerPath).exists()) forgeSetupInstallerTask(gamePath, targetVersion)

    // Setup launcher profiles
    val launcherProfilesFile = File("$gamePath/launcher_profiles.json")
    if (!launcherProfilesFile.exists()) {
        FileOutputStream(launcherProfilesFile).use { stream ->
            stream.write("{}".encodeToByteArray())
        }
    }

    // Run the installer if needed
    val forgeFolder = "$targetVersion-forge"
    if (!File(forgeFolder).exists()) {
        val command =
            "${GestorLauncher.findLocalJavaPath(optInLegacyJava)} -cp .;$installerPath;$mainInstallerPath me.xfl03.HeadlessInstaller -installClient $gamePath -progress"
        val process = Runtime.getRuntime().exec(command)
        process.waitFor()
    }

    // Rename the Forge folder
    var sourceFolderPath: String? = null
    File("$gamePath/versions").listFiles()!!.forEach { file ->
        if (file.isDirectory && file.name.contains("forge")) {
            sourceFolderPath = file.absolutePath
        }
    }
    if (sourceFolderPath == null) throw RuntimeException("Could not locate the Forge folder created by the installer")
    val outputFolderPath = "$gamePath/versions/$targetVersion-forge"
    File(sourceFolderPath!!).renameTo(File(outputFolderPath))

    // Rename the Forge version info
    File(outputFolderPath).listFiles()!!.forEach { file ->
        if (file.extension == "json") {
            File(file.absolutePath).renameTo(File("$outputFolderPath/$targetVersion-forge.json"))
        }
    }
}

fun forgeMigrateJarTask(
    /**
     * The path to the root directory of the game
     */
    gamePath: String,
    /**
     * The launched Minecraft version
     */
    targetVersion: String) {

    // Forge installs its remapped JAR in the libraries folder, so pick it from there
    var sourcePath: String? = null
    var forgeVersion: String
    File("$gamePath/libraries/net/minecraftforge/forge").listFiles()!!.forEach { file ->
        if (file.isDirectory && file.absolutePath.contains(targetVersion)) {
            forgeVersion = file.name.substring(file.name.indexOf("-") + 1, file.name.lastIndex + 1)
            sourcePath = "${file.absolutePath}/forge-$targetVersion-$forgeVersion-client.jar"
        }
    }
    if (sourcePath == null) throw RuntimeException("Could not locate Forge client remapped JAR")

    val outputPath = "$gamePath/versions/$targetVersion-forge/$targetVersion-forge.jar"
    Files.copy(Paths.get(sourcePath!!), FileOutputStream(outputPath))
}
