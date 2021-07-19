package com.gestormc.gestor.mod

/**
 * Contains all tasks related to normal mod management.
 *
 * How to use:
 * ```kotlin
 * ModTasks.installMod(...)
 * ```
 */
object ModTasks {
    /**
     * Installs the main mod.
     *
     * See the referenced function ([modInstallTask]) for extra information.
     */
    val installMod = ::modInstallTask

    /**
     * Installs the mod's dependencies.
     *
     * See the referenced function ([modDependencyInstallTask]) for extra information.
     */
    val installDependencies = ::modDependencyInstallTask

    /**
     * Updates the main mod if needed.
     *
     * See the referenced function ([modUpdateTask]) for extra information.
     */
    val updateMod = ::modUpdateTask

    /**
     * Updates the mod's dependencies if needed.
     *
     * See the referenced function ([modDependencyUpdateTask]) for extra information.
     */
    val updateDependencies = ::modDependencyUpdateTask

    /**
     * Removes the main mod as well as it's dependencies from the mods folder.
     *
     * See the referenced function ([fullRemoveTask]) for extra information.
     */
    val removeAll = ::fullRemoveTask
}
