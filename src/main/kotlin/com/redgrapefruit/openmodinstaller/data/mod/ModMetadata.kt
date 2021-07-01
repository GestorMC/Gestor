@file:Suppress("ArrayInDataClass")

package com.redgrapefruit.openmodinstaller.data.mod

import com.redgrapefruit.openmodinstaller.data.dependency.Dependency
import com.redgrapefruit.openmodinstaller.data.dependency.Incompatibility
import com.redgrapefruit.openmodinstaller.data.minecraft.MinecraftVersionSupport
import com.redgrapefruit.openmodinstaller.data.minecraft.ModLoader
import kotlinx.serialization.Serializable

/**
 * The [ModMetadata] contains all the details about the mod
 */
@Serializable
data class ModMetadata(
    /**
     * The displayed name of the mod. Does not have to be equal with the mod's ID
     */
    val name: String = "Unknown Mod Name",
    /**
     * An array of authors of the mod. Should not include contributors
     */
    val authors: Array<String> = arrayOf(),
    /**
     * The mod's description
     */
    val description: String = "No Description Provided",
    /**
     * List of [Link]s associated with this mod
     */
    val links: List<Link> = listOf(),
    /**
     * A [ModLicense] for this mod
     */
    val license: ModLicense = ModLicense.MIT,
    /**
     * A list of [MinecraftVersionSupport]'s for every currently available MC version
     */
    val minecraft: Array<MinecraftVersionSupport> = arrayOf(),
    /**
     * A list of supported [ModLoader]s.
     *
     * Most of the time, developers only support one [ModLoader], but there are some rare exceptions.
     */
    val loaders: Array<ModLoader> = arrayOf(),
    /**
     * A list of [Incompatibility]s with this mod
     */
    val incompatibilities: Array<Incompatibility> = arrayOf(),
    /**
     * A list of [Dependency]s for this mod
     */
    val dependencies: Array<Dependency> = arrayOf()
)
