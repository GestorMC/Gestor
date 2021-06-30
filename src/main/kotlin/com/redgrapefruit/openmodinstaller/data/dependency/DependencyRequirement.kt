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
enum class DependencyRequirement(private val jsonName: String) : KSerializer<DependencyRequirement> {
    /**
     * The [Dependency] is mandatory in order to run the mod
     */
    Mandatory("Mandatory"),

    /**
     * The [Dependency] is optional and can be discarded if needed
     */
    Optional("Optional");

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DependencyRequirement", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): DependencyRequirement {
        val requirement = decoder.decodeString()
        values().forEach {
            if (it.jsonName.equals(requirement, true)) return it
        }
        return Mandatory
    }

    override fun serialize(encoder: Encoder, value: DependencyRequirement) {
        encoder.encodeString(value.jsonName)
    }
}