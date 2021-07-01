package com.redgrapefruit.openmodinstaller.data.mod

import kotlinx.serialization.Serializable

/**
 * [ModLicense] contains all of your license info
 */
@Serializable
data class ModLicense(
    /**
     * The displayed name of the license.
     *
     * You can use shortened names here, like CDDN or MIT
     */
    val name: String,
    /**
     * The URL to the actual license
     */
    val url: String
) {
    companion object {
        val MIT = ModLicense("MIT", "https://mitlicense.org")
    }
}
