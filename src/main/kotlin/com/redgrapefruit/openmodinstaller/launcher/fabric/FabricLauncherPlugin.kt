package com.redgrapefruit.openmodinstaller.launcher.fabric

import com.redgrapefruit.openmodinstaller.launcher.LauncherPlugin
import com.redgrapefruit.openmodinstaller.launcher.core.SetupManager

/**
 * A [LauncherPlugin] providing FabricMC mod support
 */
object FabricLauncherPlugin : LauncherPlugin {
    override fun processCommand(
        source: String,
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String,
        jarTemplate: String
    ): String {
        // Use the Fabric JAR as the launched Minecraft JAR
        return source.replace(
            oldValue = "$root/versions/$version/$version-$jarTemplate.jar",
            newValue = "$root/versions/$version-fabric/$version-fabric.jar")
    }

    override fun onSetupEnd(root: String, version: String, optInLegacyJava: Boolean) {
        // Launch additional setup from FabricManager
        FabricManager.setupInstaller(root)
        FabricManager.runInstaller(root, version, optInLegacyJava)
        FabricManager.migrateClientJAR(root, version)
        SetupManager.setupLibraries(root, "$version-fabric")
    }
}
