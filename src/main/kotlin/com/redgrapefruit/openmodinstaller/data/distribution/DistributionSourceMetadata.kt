package com.redgrapefruit.openmodinstaller.data.distribution

import kotlinx.serialization.Serializable

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