package com.redgrapefruit.openmodinstaller.data

import kotlinx.serialization.Serializable

enum class LoaderType {
    Forge,
    Fabric,
    Quilt,
    Vanilla
}

enum class ModSource {
    Modrinth,
    CurseForge,
    Github,
    URL,
}

enum class ModpackState {
    Installed,
    Installing,
    Available
}

@Serializable
data class ModpackData(
    val packageVersion: Int,
    val version: String,
    val state: ModpackState,
    val loader: Pair<LoaderType, String>,
    val displayName: String,
    val creator: String,
    val date: Long,
    val description: String,
    val mods: Array<Pair<ModSource, String>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModpackData

        if (version != other.version) return false
        if (creator != other.creator) return false
        if (date != other.date) return false
        if (!mods.contentDeepEquals(other.mods)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = version.hashCode()
        result = 31 * result + creator.hashCode()
        result = (31 * result + date).toInt()
        result = 31 * result + mods.contentDeepHashCode()
        return result
    }
}