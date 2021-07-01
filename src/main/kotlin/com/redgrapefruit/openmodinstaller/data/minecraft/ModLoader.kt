package com.redgrapefruit.openmodinstaller.data.minecraft

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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
