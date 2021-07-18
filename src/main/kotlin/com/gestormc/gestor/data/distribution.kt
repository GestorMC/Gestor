package com.gestormc.gestor.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Used for linking the JSON file with the mod's configuration
 */
@Serializable
data class DistributedModLink(
    /**
     * The URL with the mod's configuration JSON
     */
    val modJsonURL: String
)

/**
 * This JSON data class describes metadata of the distributions hosted by the user and the mods distributed
 */
@Serializable
data class DistributionSource(
    /**
     * The [DistributionSourceMetadata] for these distributions
     */
    val meta: DistributionSourceMetadata,
    /**
     * A list of [DistributedModLink]s that this source contains
     */
    val mods: List<DistributedModLink>
)

/**
 * The [DistributionSourceMetadata] is a block inside of the [DistributionSource] describing its metadata
 */
@Serializable
data class DistributionSourceMetadata(
    /**
     * The host owning these distributions
     */
    val host: String = "Unknown Host",
    /**
     * The name of these distributions
     */
    val name: String = "Unknown Name",
    /**
     * The [DistributionSourceProvider] to determine the source of these distributions
     */
    val provider: DistributionSourceProvider = DistributionSourceProvider.Custom,
    /**
     * Do the distributions require downloading from the Internet.
     */
    val online: Boolean = true
)

/**
 * The [DistributionSourceProvider] determines the source where these distributions are hosted on the Internet
 */
enum class DistributionSourceProvider : KSerializer<DistributionSourceProvider> {
    /**
     * GitHub host. The easiest way (creating a GitHub repository as the distribution source)
     */
    GitHub,

    /**
     * GitLab host. An alternative way to [GitHub]
     */
    GitLab,

    /**
     * BitBucket host. Another alternative to [GitHub] and [GitLab], though not recommended for production use
     */
    BitBucket,

    /**
     * Mega. Host your files on the cloud publicly for download
     */
    Mega,

    /**
     * Google Drive. A Google cloud alternative to Mega with better reputation
     */
    GoogleDrive,

    /**
     * Microsoft OneDrive. Built-in cloud storage by Microsoft, integrated for Windows users
     */
    OneDrive,

    /**
     * Dropbox. Another famous cloud storage, but with only 2GB in the Free plan
     */
    Dropbox,

    /**
     * Any CDN service with your files
     */
    CDN,

    /**
     * Any other website not listed here or your own website
     */
    Custom;

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DistributionsWebsite", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): DistributionSourceProvider {
        val distributionsWebsite = decoder.decodeString()
        values().forEach {
            if (it.name.equals(distributionsWebsite, true)) return it
        }
        return Custom
    }

    override fun serialize(encoder: Encoder, value: DistributionSourceProvider) {
        encoder.encodeString(value.name)
    }
}
