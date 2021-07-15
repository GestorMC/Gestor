package com.redgrapefruit.openmodinstaller.launcher.fabric

import com.redgrapefruit.openmodinstaller.launcher.core.LaunchCommandTweak

/**
 * A [LaunchCommandTweak] that uses the Fabric JAR instead of the normal JAR
 */
object FabricLaunchCommandTweak : LaunchCommandTweak {
    override fun apply(source: String, gamePath: String, targetVersion: String, jarTemplate: String): String {
        return source.replace(
            oldValue = "$gamePath/versions/$targetVersion/$targetVersion-$jarTemplate.jar",
            newValue = "$gamePath/versions/$targetVersion-fabric/$targetVersion-fabric.jar")
    }
}
