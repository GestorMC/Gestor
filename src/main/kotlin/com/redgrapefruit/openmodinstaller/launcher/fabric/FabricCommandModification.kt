package com.redgrapefruit.openmodinstaller.launcher.fabric

import com.redgrapefruit.openmodinstaller.launcher.core.CommandModification

/**
 * A [CommandModification] for the FabricMC Loader that allows the use of Fabric client JARs instead of normal vanilla JARs.
 *
 * Please do note that server FabricMC will not ever be supported. Vanilla server support may also be removed in the future.
 */
object FabricCommandModification : CommandModification {
    override fun modify(source: String, targetVersion: String, gamePath: String, jarTemplate: String): String {
        return source.replace(
            oldValue = "$gamePath/versions/$targetVersion/$targetVersion-$jarTemplate.jar",
            newValue = "$gamePath/versions/$targetVersion-fabric/$targetVersion-fabric.jar")
    }
}
