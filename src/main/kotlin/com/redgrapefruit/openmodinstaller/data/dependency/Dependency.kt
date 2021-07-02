package com.redgrapefruit.openmodinstaller.data.dependency

import com.redgrapefruit.openmodinstaller.data.mod.Link
import kotlinx.serialization.Serializable

/**
 * A [Dependency] represents a first-party or third-party mod that the main mod requires
 * to also be installed in order to work
 */
@Serializable
data class Dependency(
    /**
     * The name of the dependency
     */
    val name: String = "Unknown Name",
    /**
     * The ID of the dependency
     */
    val id: String = "Unknown Mod ID",
    /**
     * A list of external links to the dependency
     */
    val links: List<Link> = listOf(),
    /**
     * Is the dependency made by a third-party author
     */
    val thirdParty: Boolean = true,
    /**
     * The [DependencyRequirement] of this dependency
     */
    val requirement: DependencyRequirement = DependencyRequirement.Mandatory,
    /**
     * The minimal version of the dependency that works with the mod.
     *
     * You usually want to download exactly this version because it's tested to run by the developers of the mod.
     */
    val minVersion: String = "Version Not Specified",
    /**
     * Is the dependency's JAR packaged inside of the main mod's JAR
     */
    val jarInJar: Boolean = false,
    /**
     * Is the installation process of this dependency fully automated by the installer
     */
    val automatic: Boolean = true
)
