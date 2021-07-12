package com.redgrapefruit.openmodinstaller.launcher

import com.sun.security.auth.module.NTSystem
import java.io.File

/**
 * The main class for launching the game
 */
class OpenLauncher private constructor(private val root: String) {
    /**
     * Sets up a new game instance.
     *
     * If making from scratch, run [clear] beforehand
     */
    fun setup(
        /**
         * Minecraft version
         */
        version: String,
        /**
         * Opt in legacy AdoptOpenJRE 8
         */
        optInLegacyJava: Boolean = false) {

        SetupManager.setupVersionInfo(root, version)
        SetupManager.setupLibraries(root, version)
        SetupManager.setupJAR(root, version)
        SetupManager.setupJava(optInLegacyJava)
    }

    /**
     * Removes the current game instance
     */
    fun clear() {
        val rootFile = File(root)

        rootFile.deleteRecursively()
        rootFile.mkdir()
    }

    fun launch(username: String, maxMemory: Int, jvmArgs: String = "") {

    }

    companion object {
        /**
         * Creates a new instance of [OpenLauncher]
         */
        fun create(
            /**
             * Game's root folder. Win AppData by default
             */
            root: String = "C:/Users/${NTSystem().name}/AppData/Roaming/.minecraft"): OpenLauncher {
            return OpenLauncher(root)
        }
    }
}
