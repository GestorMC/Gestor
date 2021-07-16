package com.redgrapefruit.openmodinstaller.launcher.core

import com.redgrapefruit.openmodinstaller.launcher.OpenLauncher

/**
 * [CommandModification] are an advanced system to support changes to the Minecraft launch command created by the [OpenLauncher]
 *
 * For instance, you create a [CommandModification] for a module for the installer that uses another JAR in another directory.
 * And you replace the source path to the JAR with your own.
 *
 * The modifications are applied via the `modify` function.
 *
 * The modifications are not fully compatible with each other, except for modifications which add elements, not change or remove them.
 *
 * However, the use of multiple [CommandModification]s is fully supported in case it is ever needed and how it works
 * is basically they are applied on top of each other.
 */
interface CommandModification {
    /**
     * Performs the modification on the [source] command.
     *
     * This function also contains some contextual information in case you need it.
     *
     * The returned value is the processed command by which the previous version is replaced.
     */
    fun modify(source: String, targetVersion: String, gamePath: String, jarTemplate: String): String
}
