package com.redgrapefruit.openmodinstaller.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * The version manifest contains data about all Minecraft versions **ever**
 */
@Serializable
data class VersionManifest(
    /**
     * The head [ManifestLatestBlock]
     */
    val latest: ManifestLatestBlock,
    /**
     * A list of [ManifestReleaseEntry]s for each of the 500+ Minecraft releases
     */
    val versions: List<ManifestReleaseEntry>
)

@Serializable
data class ManifestLatestBlock(
    /**
     * The ID of the official latest Minecraft release
     */
    val release: String,
    /**
     * The ID of the latest development snapshot of Minecraft
     */
    val snapshot: String
)

/**
 * An entry for each release in the version manifest
 */
@Serializable
data class ManifestReleaseEntry(
    /**
     * The ID/name of the release
     */
    val id: String,
    /**
     * A [ManifestReleaseType] for this release
     */
    val type: ManifestReleaseType,
    /**
     * A direct URL to a [id].json file containing most of the necessary metadata
     */
    val url: String,
    /**
     * A formatted time when this release was uploaded to the manifest
     */
    val time: String,
    /**
     * The exact formatted time when this release was first uploaded for use by players
     */
    val releaseTime: String
)

/**
 * An official release type by Mojang presented in the version manifest
 */
@Suppress("EnumEntryName")
enum class ManifestReleaseType : KSerializer<ManifestReleaseType> {
    /**
     * An official approved and tested Minecraft release
     */
    release,

    /**
     * A development snapshot for a release
     */
    snapshot,

    /**
     * Old beta version of the game from 2011
     */
    old_beta,

    /**
     * Old alpha version of the game from 2009 and 2010
     */
    old_alpha;

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ManifestReleaseType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ManifestReleaseType {
        val type = decoder.decodeString()
        values().forEach {
            if (it.name.equals(type, true)) return it
        }
        return release
    }

    override fun serialize(encoder: Encoder, value: ManifestReleaseType) {
        encoder.encodeString(value.name)
    }
}
