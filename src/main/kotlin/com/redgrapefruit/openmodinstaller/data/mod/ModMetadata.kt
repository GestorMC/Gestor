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
     * A list of authors of the mod. Should not include contributors
     */
    val authors: List<String> = listOf(),
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
    val minecraft: List<MinecraftVersionSupport> = listOf(),
    /**
     * A list of supported [ModLoader]s.
     *
     * Most of the time, developers only support one [ModLoader], but there are some rare exceptions.
     */
    val loaders: List<ModLoader> = listOf(),
    /**
     * A list of [Incompatibility]s with this mod
     */
    val incompatibilities: List<Incompatibility> = listOf(),
    /**
     * A list of [Dependency]s for this mod
     */
    val dependencies: List<Dependency> = listOf(),
    /**
     * A link to a Markdown file representing the full page for this mod
     */
    val page: String = ""
)
