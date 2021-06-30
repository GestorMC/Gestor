package com.redgrapefruit.openmodinstaller.data.mod

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * The [ModDevelopmentState] represents the current state of the mod from the developers' POV.
 *
 * Please set this parameter honestly to avoid misleading info that the players will believe in.
 *
 * It is important.
 */
enum class ModDevelopmentState(private val jsonName: String) : KSerializer<ModDevelopmentState> {
    /**
     * In Development means that the mod is actively developed, but hasn't been released as Stable yet
     */
    InDevelopment("InDevelopment"),

    /**
     * Ready means that the mod has been released as Stable and is ready for use in modpacks etc.
     */
    Ready("Ready"),

    /**
     * Developed means that the mod has been released as Stable and will be
     * receiving updates, patches, bugfixes etc. from the developers
     */
    Developed("Developed"),

    /**
     * Maintained means that the mod has been released as Stable, won't be receiving
     * major content updates, only patches, new MC version updates and bugfixes.
     *
     * Maintenance mode is nothing to fear, just don't expect too much from the developer.
     */
    Maintained("Maintained"),

    /**
     * Finished means that the mod has been released as Stable, won't be receiving
     * **any** updates except new MC version updates.
     *
     * Finished generally means that the mod is mostly polished and clear of bugs, so no more maintenance is required
     * to operate except updating to new MC versions.
     */
    Finished("Finished"),

    /**
     * Abandoned means that the mod may or may not has been released as Stable and won't be
     * receiving any updates **at all**.
     *
     * Be cautious when downloading such mods.
     */
    Abandoned("Abandoned");

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ModDevelopmentState", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ModDevelopmentState {
        val modDevelopmentState = decoder.decodeString()
        values().forEach {
            if (it.jsonName.equals(modDevelopmentState, true)) return it
        }
        return Ready
    }

    override fun serialize(encoder: Encoder, value: ModDevelopmentState) {
        encoder.encodeString(value.jsonName)
    }
}
