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
enum class DistributionSourceProvider(private val jsonName: String) : KSerializer<DistributionSourceProvider> {
    /**
     * GitHub host. The easiest way (creating a GitHub repository as the distribution source)
     */
    GitHub("GitHub"),

    /**
     * GitLab host. An alternative way to [GitHub]
     */
    GitLab("GitLab"),

    /**
     * BitBucket host. Another alternative to [GitHub] and [GitLab], though not recommended for production use
     */
    BitBucket("BitBucket"),

    /**
     * Mega. Host your files on the cloud publicly for download
     */
    Mega("Mega"),

    /**
     * Google Drive. A Google cloud alternative to Mega with better reputation
     */
    GoogleDrive("GoogleDrive"),

    /**
     * Microsoft OneDrive. Built-in cloud storage by Microsoft, integrated for Windows users
     */
    OneDrive("OneDrive"),

    /**
     * Dropbox. Another famous cloud storage, but with only 2GB in the Free plan
     */
    Dropbox("Dropbox"),

    /**
     * Any CDN service with your files
     */
    CDN("CDN"),

    /**
     * Any other website not listed here or your own website
     */
    Custom("Custom");

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DistributionsWebsite", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): DistributionSourceProvider {
        val distributionsWebsite = decoder.decodeString()
        values().forEach {
            if (it.jsonName.equals(distributionsWebsite, true)) return it
        }
        return Custom
    }

    override fun serialize(encoder: Encoder, value: DistributionSourceProvider) {
        encoder.encodeString(value.jsonName)
    }
}