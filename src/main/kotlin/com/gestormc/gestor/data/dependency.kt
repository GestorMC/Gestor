package com.gestormc.gestor.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A [Dependency] represents a first-party or third-party mod that the main mod requires
 * to also be installed in order to work
 */
@Serializable
data class Dependency(
    /**
     * The name of the dependency
     */
    val name: String = "Unknown Name",
    /**
     * The ID of the dependency
     */
    val id: String = "Unknown Mod ID",
    /**
     * A list of external links to the dependency
     */
    val links: List<Link> = listOf(),
    /**
     * Is the dependency made by a third-party author
     */
    val thirdParty: Boolean = true,
    /**
     * The [DependencyRequirement] of this dependency
     */
    val requirement: DependencyRequirement = DependencyRequirement.Mandatory,
    /**
     * The minimal version of the dependency that works with the mod.
     *
     * You usually want to download exactly this version because it's tested to run by the developers of the mod.
     */
    val minVersion: String = "Version Not Specified",
    /**
     * Is the dependency's JAR packaged inside of the main mod's JAR
     */
    val jarInJar: Boolean = false,
    /**
     * Is the installation process of this dependency fully automated by the installer
     */
    val automatic: Boolean = true
)

/**
 * How much the [Dependency] is required
 */
enum class DependencyRequirement : KSerializer<DependencyRequirement> {
    /**
     * The [Dependency] is mandatory in order to run the mod
     */
    Mandatory,

    /**
     * The [Dependency] is optional and can be discarded if needed
     */
    Optional;

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DependencyRequirement", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): DependencyRequirement {
        val requirement = decoder.decodeString()
        values().forEach {
            if (it.name.equals(requirement, true)) return it
        }
        return Optional
    }

    override fun serialize(encoder: Encoder, value: DependencyRequirement) {
        encoder.encodeString(value.name)
    }
}

/**
 * A [Incompatibility] indicates that the mod is incompatible with some other mod
 * for technical reasons
 */
@Serializable
data class Incompatibility(
    /**
     * The name of the incompatible mod
     */
    val incompatibleModName: String = "Unknown Mod Name",
    /**
     * The mod ID of the incompatible mod
     */
    val incompatibleModID: String = "Unknown Mod ID",
    /**
     * The reason why the mod is incompatible with the main mod
     */
    val reason: String = "Reason Not Provided",
    /**
     * If there are exceptions, fill this list with versions of the incompatible
     * mod which are fully/partially compatible
     */
    val compatibleVersions: List<String> = listOf()
)
