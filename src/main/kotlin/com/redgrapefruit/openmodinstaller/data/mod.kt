package com.redgrapefruit.openmodinstaller.data

import com.redgrapefruit.openmodinstaller.data.distribution.DistributedModLink
import com.redgrapefruit.openmodinstaller.data.distribution.DistributionSource
import com.redgrapefruit.openmodinstaller.data.minecraft.MinecraftVersionSupport
import com.redgrapefruit.openmodinstaller.data.minecraft.ModLoader
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A link to an external resource that this mod comes from.
 *
 * Common usage: GitHub, CurseForge, GitLab etc.
 */
@Serializable
data class Link(
    /**
     * The displayed name of the resource
     */
    val name: String,
    /**
     * The link to the resource
     */
    val url: String
)

/**
 * This JSON data class describes all details of a certain distributed mod.
 *
 * A JSON serialized into a [Mod] instance must be linked in the [DistributionSource] via a [DistributedModLink]
 */
@Serializable
data class Mod(
    /**
     * The [ModMetadata] linked to this mod
     */
    val meta: ModMetadata,
    /**
     * The [ReleaseEntry]s of the main mod
     */
    val main: List<ReleaseEntry> = listOf(),
    /**
     * The [DependencyReleaseEntryWrapper]s (workaround) of the dependencies
     */
    val dependencies: List<DependencyReleaseEntryWrapper> = listOf()
)

/**
 * [ModLicense] contains all of your license info
 */
@Serializable
data class ModLicense(
    /**
     * The displayed name of the license.
     *
     * You can use shortened names here, like CDDN or MIT
     */
    val name: String,
    /**
     * The URL to the actual license
     */
    val url: String
) {
    companion object {
        val MIT = ModLicense("MIT", "https://mitlicense.org")
    }
}

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

/**
 * An entry containing the details about a certain release of the mod or its dependencies
 */
@Serializable
data class ReleaseEntry(
    /**
     * The direct URL to the JAR file
     */
    val url: String = "Unknown URL",
    /**
     * The [ReleaseType] of this release
     */
    val releaseType: ReleaseType = ReleaseType.Stable,
    /**
     * The latest version of the mod required for the updating system to work.
     *
     * You can put ~ as the value if the version isn't specified in `fabric.mod.json`,
     * which disables the updating system except for the SHA-512 check.
     *
     * Modders, **for the love of god, put the goddamn version** into the `fabric.mod.json`
     */
    val version: String = "~",
    /**
     * The changelog for this release.
     *
     * You can provide the text with the changelog
     */
    val changelog: String = ""
)

/**
 * A wrapper for storing [ReleaseEntry]s for dependencies
 */
@Serializable
data class DependencyReleaseEntryWrapper(
    /**
     * The name of the actual dependency
     */
    val id: String,
    /**
     * The [ReleaseEntry]s
     */
    val entries: List<ReleaseEntry>
)

/**
 * A [ReleaseType] determines the stability of a certain release.
 */
enum class ReleaseType : KSerializer<ReleaseType> {
    /**
     * Long Term Support of a certain Stable release
     */
    LTS,

    /**
     * Stable release ready for production use
     */
    Stable,

    /**
     * Release candidate with almost full feature parity and stability,
     * undergoing final testing and polishing from the developer(s)
     */
    RC,

    /**
     * A preview of a certain Stable release with most things finished and ironed out.
     */
    Preview,

    /**
     * A release from the early access program (EAP) with features under development.
     */
    EAP,

    /**
     * A beta with many features complete, but some bugs left
     */
    Beta,

    /**
     * An alpha with some features complete and lots of stuff to be ironed out
     */
    Alpha,

    /**
     * An incomplete preview of a beta release.
     */
    BetaPreview,

    /**
     * An incomplete preview of an alpha release.
     */
    AlphaPreview,

    /**
     * A snapshot of a release. Not recommended since a snapshot doesn't indicate
     * the exact stability of a certain release. Inspired by Maven conventions.
     */
    Snapshot,

    /**
     * A nightly build automatically produced by the Continuous Integration of the project.
     *
     * Highly, highly untested and unstable.
     */
    Nightly;

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ReleaseType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ReleaseType {
        val releaseType = decoder.decodeString()
        values().forEach {
            if (it.name.equals(releaseType, true)) return it
        }
        return Stable
    }

    override fun serialize(encoder: Encoder, value: ReleaseType) {
        encoder.encodeString(value.name)
    }

    /**
     * Searches for the entry by the [ReleaseType] in the [Mod]
     */
    fun getEntry(isMain: Boolean, mod: Mod, depId: String = ""): ReleaseEntry {
        if (isMain) {
            mod.main.forEach { entry ->
                if (entry.releaseType == this) {
                    return entry
                }
            }
        } else {
            mod.dependencies.forEach { wrapper ->
                if (wrapper.id == depId) {
                    wrapper.entries.forEach { entry ->
                        if (entry.releaseType == this) {
                            return entry
                        }
                    }
                }
            }
        }
        throw RuntimeException("Couldn't find entry")
    }
}
