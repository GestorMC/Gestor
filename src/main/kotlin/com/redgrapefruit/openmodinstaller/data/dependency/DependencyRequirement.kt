package com.redgrapefruit.openmodinstaller.data.dependency

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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