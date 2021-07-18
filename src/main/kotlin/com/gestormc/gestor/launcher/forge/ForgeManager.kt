package com.gestormc.gestor.launcher.forge

import com.gestormc.gestor.launcher.ModloaderManager
import com.gestormc.gestor.task.downloadFile
import java.io.File

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
        val installerPath = "$gamePath/openmodinstaller/forge/installer/forge-installer-headless-1.0.1.jar"
        if (File(installerPath).exists()) return
        // Download the headless installer
        downloadFile(HEADLESS_INSTALLER_URL, installerPath)


    }

    override fun runInstaller(gamePath: String, targetVersion: String, optInLegacyJava: Boolean) {

        // Check if the installer exists, if not, run the setup
        val installerPath = "$gamePath/openmodinstaller/forge/installer/forge-installer-headless-1.0.1.jar"
        if (!File(installerPath).exists()) setupInstaller(gamePath, targetVersion)

        // Run the installer

    }

    override fun migrateClientJAR(gamePath: String, targetVersion: String) {

    }
}
