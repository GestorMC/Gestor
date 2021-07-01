package com.redgrapefruit.openmodinstaller.data.dependency

import kotlinx.serialization.Serializable

/**
 * A [Incompatibility] indicates that the mod is incompatible with some other mod
 * for technical reasons
 */
@Serializable
data class Incompatibility(
    /**
     * The name of the incompatible mod
     */
    val incompatibleModName: String = "Unknown Mod Name",
    /**
     * The mod ID of the incompatible mod
     */
    val incompatibleModID: String = "Unknown Mod ID",
    /**
     * The reason why the mod is incompatible with the main mod
     */
    val reason: String = "Reason Not Provided",
    /**
     * If there are exceptions, fill this list with versions of the incompatible
     * mod which are fully/partially compatible
     */
    val compatibleVersions: List<String> = listOf()
)
