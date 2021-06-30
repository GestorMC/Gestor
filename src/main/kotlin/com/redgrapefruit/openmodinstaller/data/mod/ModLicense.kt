package com.redgrapefruit.openmodinstaller.data.mod

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * The [ModLicense] represents an OSS license that this mod falls in.
 *
 * The list has been acquired on the [opensource.org](opensource.org) website + AGPL.
 *
 * We **will never** support proprietary licenses and paid mods.
 */
enum class ModLicense(private val jsonName: String) : KSerializer<ModLicense> {
    /**
     * Apache license 2.0
     */
    Apache2("Apache2"),

    /**
     * BSD license version 3
     */
    BSD3("BSD3"),

    /**
     * BSD license version 2
     */
    BSD2("BSD2"),

    /**
     * GNU General Public License
     */
    GPL("GPL"),

    /**
     * Lesser GNU General Public License
     */
    LGPL("LGPL"),

    /**
     * Affero GNU General Public License
     */
    AGPL("AGPL"),

    /**
     * MIT License. Most commonly used and highly recommended for any mod projects
     */
    MIT("MIT"),

    /**
     * Mozilla Public License version 2
     */
    MPL2("MPL2"),

    /**
     * Common Development and Distribution License version 1
     */
    CDDL("CDDL"),

    /**
     * Eclipse Public License version 2
     */
    EPL("EPL");

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ModLicense", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ModLicense {
        val modLicense = decoder.decodeString()
        values().forEach {
            if (it.jsonName.equals(modLicense, true)) return it
        }
        return MIT
    }

    override fun serialize(encoder: Encoder, value: ModLicense) {
        encoder.encodeString(value.jsonName)
    }
}
