package com.redgrapefruit.openmodinstaller.data.distribution

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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