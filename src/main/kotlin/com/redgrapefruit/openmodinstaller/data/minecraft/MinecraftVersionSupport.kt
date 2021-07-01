package com.redgrapefruit.openmodinstaller.data.minecraft

import com.redgrapefruit.openmodinstaller.data.mod.ReleaseType
import kotlinx.serialization.Serializable

/**
 * The [MinecraftVersionSupport] describes all details about the mod supporting a certain Minecraft version
 */
@Serializable
data class MinecraftVersionSupport(
    /**
     * The actual version of Minecraft.
     */
    val version: String = "1.17",
    /**
     * The [ReleaseType] of that Minecraft release.
     */
    val releaseType: ReleaseType = ReleaseType.Stable,
    /**
     * The [MinecraftVersionSupportState] for this release.
     */
    val support: MinecraftVersionSupportState = MinecraftVersionSupportState.Active
)
