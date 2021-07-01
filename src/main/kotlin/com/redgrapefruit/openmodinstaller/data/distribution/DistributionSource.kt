package com.redgrapefruit.openmodinstaller.data.distribution

import kotlinx.serialization.Serializable

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


