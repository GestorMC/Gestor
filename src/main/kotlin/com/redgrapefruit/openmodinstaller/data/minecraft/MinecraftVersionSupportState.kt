package com.redgrapefruit.openmodinstaller.data.minecraft

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A [MinecraftVersionSupportState] determines how much attention a certain MC release is receiving from the developers
 */
enum class MinecraftVersionSupportState(private val jsonName: String) : KSerializer<MinecraftVersionSupportState> {
    /**
     * The support is active and 100% feature parity is guaranteed
     */
    Active("Active"),

    /**
     * The support is work-in-progress and 100% feature parity
     */
    Upcoming("Upcoming"),

    /**
     * The support is maintained, but new features won't make their way into this MC version
     */
    Maintained("Maintained"),

    /**
     * This version is no longer supported by the developers.
     */
    NoLongerSupported("NoLongerSupported");

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ModMinecraftVersionSupport", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): MinecraftVersionSupportState {
        val modMinecraftVersionSupport = decoder.decodeString()
        values().forEach {
            if (it.jsonName.equals(modMinecraftVersionSupport, true)) return it
        }
        return Active
    }

    override fun serialize(encoder: Encoder, value: MinecraftVersionSupportState) {
        encoder.encodeString(value.jsonName)
    }
}
