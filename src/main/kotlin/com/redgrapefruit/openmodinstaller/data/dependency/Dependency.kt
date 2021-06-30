package com.redgrapefruit.openmodinstaller.data.dependency

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
     * The CurseForge link of the dependency
     */
    val curseforge: String = "CurseForge Not Provided",
    /**
     * The sources of the dependency, GitHub, GitLab or BitBucket
     */
    val sources: String = "Sources Not Provided",
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
