package com.gestormc.gestor.launcher.forge

import com.gestormc.gestor.launcher.ModloaderManager
import com.gestormc.gestor.task.downloadFile
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.FileInputStream

/**
 * The modloader manager for Minecraft Forge.
 *
 * The unofficial headless installer is used instead of the GUI installer.
 */
object ForgeManager : ModloaderManager {
    /**
     * The URL to downloading the JAR of the latest ForgeHeadless release
     */
    private const val HEADLESS_INSTALLER_URL = "https://github.com/xfl03/ForgeInstallerHeadless/releases/download/1.0.1/forge-installer-headless-1.0.1.jar"


    override fun setupInstaller(gamePath: String, targetVersion: String) {
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
            downloadFile("https://files.minecraftforge.net/net/minecraftforge/forge/index_$targetVersion.html", indexPath)
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

    }

    override fun runInstaller(gamePath: String, targetVersion: String, optInLegacyJava: Boolean) {

        // Check if the installer exists, if not, run the setup
        val installerPath = "$gamePath/openmodinstaller/forge/installer/headless/forge-installer-headless-1.0.1.jar"
        if (!File(installerPath).exists()) setupInstaller(gamePath, targetVersion)

        // Run the installer

    }

    override fun migrateClientJAR(gamePath: String, targetVersion: String) {

    }
}
