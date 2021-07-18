package com.gestormc.gestor.launcher

/**
 * Contains all commons for each modloader manager object to implement
 */
interface ModloaderManager {
    /**
     * Sets up the installer for this modloader
     */
    fun setupInstaller(gamePath: String, targetVersion: String)

    /**
     * Runs the installer for this modloader
     */
    fun runInstaller(gamePath: String, targetVersion: String, optInLegacyJava: Boolean = false)

    /**
     * Copies over the vanilla client JAR to the dedicated folder
     */
    fun migrateClientJAR(gamePath: String, targetVersion: String)
}
