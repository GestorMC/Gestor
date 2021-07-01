package com.redgrapefruit.openmodinstaller.data.mod

import kotlinx.serialization.Serializable

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
