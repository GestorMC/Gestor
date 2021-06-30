@file:Suppress("ArrayInDataClass")

package com.redgrapefruit.openmodinstaller.data.mod

import com.redgrapefruit.openmodinstaller.data.distribution.DistributedModLink
import com.redgrapefruit.openmodinstaller.data.distribution.DistributionSource
import kotlinx.serialization.Serializable

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
    val main: Array<ReleaseEntry> = arrayOf(),
    /**
     * The [DependencyReleaseEntryWrapper]s (workaround) of the dependencies
     */
    val dependencies: Array<DependencyReleaseEntryWrapper> = arrayOf()
)
