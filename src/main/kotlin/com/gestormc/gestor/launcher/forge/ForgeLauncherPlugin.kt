package com.gestormc.gestor.launcher.forge

import com.gestormc.gestor.launcher.LauncherPlugin
import com.gestormc.gestor.launcher.core.LibraryManager
import com.gestormc.gestor.util.plusAssign
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.FileInputStream

/**
 * A [LauncherPlugin] providing MinecraftForge mod support
 */
object ForgeLauncherPlugin : LauncherPlugin {
    override fun processCommand(
        source: String,
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String,
        jarTemplate: String
    ): String {
        // Use the Forge remapped JAR as the launched JAR and fix main class
        // Load version info's
        val forgeVersionInfo: JsonObject
        val vanillaVersionInfo: JsonObject
        FileInputStream("$root/versions/$version-forge/$version-forge.json").use { stream ->
            forgeVersionInfo = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }
        FileInputStream("$root/versions/$version/$version.json").use { stream ->
            vanillaVersionInfo = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }

        return source
            .replace("$root/versions/$version/$version-$jarTemplate.jar", "$root/versions/$version-forge/$version-forge.jar")
            .replace(vanillaVersionInfo["mainClass"]!!.jsonPrimitive.content, forgeVersionInfo["mainClass"]!!.jsonPrimitive.content)
    }

    override fun processClasspath(
        classpath: String,
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String
    ): String {
        // Load Forge version info
        val versionInfoObject: JsonObject
        FileInputStream("$root/versions/$version-forge/$version-forge.json").use { stream ->
            versionInfoObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }
        // Add all libs from there
        val builder = StringBuilder(classpath)
        builder += LibraryManager.getLibrariesFormatted(root, versionInfoObject)

        return builder.toString()
    }

    override fun onSetupEnd(root: String, version: String, optInLegacyJava: Boolean) {
        // Launch extra setup from ForgeManager
        // Library-checking is not needed, unlike Fabric, since the Forge installation process already does the job
        ForgeManager.setupInstaller(root, version)
        ForgeManager.runInstaller(root, version, optInLegacyJava)
        ForgeManager.migrateClientJAR(root, version)
    }
}
