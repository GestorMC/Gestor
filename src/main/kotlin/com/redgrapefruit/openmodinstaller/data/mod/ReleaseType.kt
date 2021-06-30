package com.redgrapefruit.openmodinstaller.data.mod

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A [ReleaseType] determines the stability of a certain release.
 */
enum class ReleaseType(private val jsonName: String) : KSerializer<ReleaseType> {
    /**
     * Long Term Support of a certain Stable release
     */
    LTS("LTS"),

    /**
     * Stable release ready for production use
     */
    Stable("Stable"),

    /**
     * Release candidate with almost full feature parity and stability,
     * undergoing final testing and polishing from the developer(s)
     */
    RC("RC"),

    /**
     * A preview of a certain Stable release with most things finished and ironed out.
     */
    Preview("Preview"),

    /**
     * A release from the early access program (EAP) with features under development.
     */
    EAP("EAP"),

    /**
     * A beta with many features complete, but some bugs left
     */
    Beta("Beta"),

    /**
     * An alpha with some features complete and lots of stuff to be ironed out
     */
    Alpha("Alpha"),

    /**
     * An incomplete preview of a beta release.
     */
    BetaPreview("BetaPreview"),

    /**
     * An incomplete preview of an alpha release.
     */
    AlphaPreview("AlphaPreview"),

    /**
     * A snapshot of a release. Not recommended since a snapshot doesn't indicate
     * the exact stability of a certain release. Inspired by Maven conventions.
     */
    Snapshot("Snapshot"),

    /**
     * A nightly build automatically produced by the Continuous Integration of the project.
     *
     * Highly, highly untested and unstable.
     */
    Nightly("Nightly");

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ReleaseType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ReleaseType {
        val releaseType = decoder.decodeString()
        values().forEach {
            if (it.jsonName.equals(releaseType, true)) return it
        }
        return Stable
    }

    override fun serialize(encoder: Encoder, value: ReleaseType) {
        encoder.encodeString(value.jsonName)
    }

    /**
     * Searches for the entry by the [ReleaseType] in the [Mod]
     */
    fun getEntry(isMain: Boolean, mod: Mod, depId: String = ""): ReleaseEntry {
        if (isMain) {
            mod.main.forEach { entry ->
                if (entry.releaseType == this) {
                    return entry
                }
            }
        } else {
            mod.dependencies.forEach { wrapper ->
                if (wrapper.id == depId) {
                    wrapper.entries.forEach { entry ->
                        if (entry.releaseType == this) {
                            return entry
                        }
                    }
                }
            }
        }
        throw RuntimeException("Couldn't find entry")
    }
}
