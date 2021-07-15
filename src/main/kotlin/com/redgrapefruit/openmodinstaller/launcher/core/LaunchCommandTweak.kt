package com.redgrapefruit.openmodinstaller.launcher.core

/**
 * A tweak to the launch command modifies the command, adds different options etc.
 *
 * An example of a [LaunchCommandTweak] would be using JARs for Fabric, Forge and Quilt since they're named differently.
 */
interface LaunchCommandTweak {
    /**
     * Tweaks the [source] command and returns the output result
     */
    fun apply(source: String, gamePath: String, targetVersion: String, jarTemplate: String): String
}
