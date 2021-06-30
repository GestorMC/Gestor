package com.redgrapefruit.openmodinstaller.data.distribution

import kotlinx.serialization.Serializable

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