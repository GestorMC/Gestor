@file:Suppress("ArrayInDataClass")

package com.redgrapefruit.openmodinstaller.data.mod

import kotlinx.serialization.Serializable

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
    val entries: Array<ReleaseEntry>
)
