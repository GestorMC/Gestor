package com.gestormc.gestor.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * The [MinecraftVersionSupport] describes all details about the mod supporting a certain Minecraft version
 */
@Serializable
data class MinecraftVersionSupport(
    /**
     * The actual version of Minecraft.
     */
    val version: String = "1.17",
    /**
     * The [ReleaseType] of that Minecraft release.
     */
    val releaseType: ReleaseType = ReleaseType.Stable,
    /**
     * The [MinecraftVersionSupportState] for this release.
     */
    val support: MinecraftVersionSupportState = MinecraftVersionSupportState.Active
)

/**
 * A [MinecraftVersionSupportState] determines how much attention a certain MC release is receiving from the developers
 */
enum class MinecraftVersionSupportState : KSerializer<MinecraftVersionSupportState> {
    /**
     * The support is active and 100% feature parity is guaranteed
     */
    Active,

    /**
     * The support is work-in-progress and 100% feature parity
     */
    Upcoming,

    /**
     * The support is maintained, but new features won't make their way into this MC version
     */
    Maintained,

    /**
     * This version is no longer supported by the developers.
     */
    NoLongerSupported;

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ModMinecraftVersionSupport", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): MinecraftVersionSupportState {
        val modMinecraftVersionSupport = decoder.decodeString()
        values().forEach {
            if (it.name.equals(modMinecraftVersionSupport, true)) return it
        }
        return Active
    }

    override fun serialize(encoder: Encoder, value: MinecraftVersionSupportState) {
        encoder.encodeString(value.name)
    }
}

/**
 * A modloader used by the mod.
 */
enum class ModLoader : KSerializer<ModLoader> {
    /**
     * The biggest and most famous modloader - Forge.
     */
    MinecraftForge,

    /**
     * A lightweight, powerful and modern modloader - Fabric.
     */
    FabricMC,

    /**
     * A fork of Fabric highly work-in-progress with lots of future promises.
     */
    QuiltMC;

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ModLoader", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ModLoader {
        val modLoader = decoder.decodeString()
        values().forEach {
            if (it.name.equals(modLoader, true)) return it
        }
        return MinecraftForge
    }

    override fun serialize(encoder: Encoder, value: ModLoader) {
        encoder.encodeString(value.name)
    }
}
