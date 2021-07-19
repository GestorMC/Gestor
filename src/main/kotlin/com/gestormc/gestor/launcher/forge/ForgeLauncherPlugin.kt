package com.gestormc.gestor.launcher.forge

import com.gestormc.gestor.launcher.LauncherPlugin
import com.gestormc.gestor.launcher.core.LibraryManager
import com.gestormc.gestor.util.plusAssign
import kotlinx.serialization.json.*
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
            .replace(
                "$root/versions/$version/$version-$jarTemplate.jar",
                "$root/versions/$version-forge/$version-forge.jar"
            )
            .replace(
                vanillaVersionInfo["mainClass"]!!.jsonPrimitive.content,
                forgeVersionInfo["mainClass"]!!.jsonPrimitive.content
            )
    }

    override fun generateLibraryReplacers(
        jvmArguments: String,
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String
    ): Map<String, (String) -> String> {
        // Parse version info's
        val vanillaVersionInfo: JsonObject
        val forgeVersionInfo: JsonObject
        FileInputStream("$root/versions/$version/$version.json").use { stream ->
            vanillaVersionInfo = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }
        FileInputStream("$root/versions/$version-forge/$version-forge.json").use { stream ->
            forgeVersionInfo = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }
        // Read versions using the utility
        val srcVersion = readVersion(vanillaVersionInfo, "log4j")
        val outVersion = readVersion(forgeVersionInfo, "log4j")

        return mapOf("log4j" to { source -> source.replace(srcVersion, outVersion) })
    }

    override fun processGameArguments(
        arguments: String,
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String
    ): String {
        // Read Forge version info with arguments
        val versionInfoObject: JsonObject
        FileInputStream("$root/versions/$version-forge/$version-forge.json").use { stream ->
            versionInfoObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }
        val argumentList = versionInfoObject["arguments"]!!.jsonObject["game"]!!.jsonArray

        val builder = StringBuilder(arguments)
        argumentList.forEach { argument -> builder += " ${argument.jsonPrimitive.content}" } // put all arguments
        return builder.toString()
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
        builder += LibraryManager.getLibrariesFormatted(root, versionInfoObject, exceptions = setOf("log4j"))

        return builder.toString()
    }

    override fun onSetupEnd(root: String, version: String, optInLegacyJava: Boolean) {
        // Launch extra setup from ForgeManager
        // Library-checking is not needed, unlike Fabric, since the Forge installation process already does the job
        ForgeManager.setupInstaller(root, version)
        ForgeManager.runInstaller(root, version, optInLegacyJava)
        ForgeManager.migrateClientJAR(root, version)
    }

    /**
     * Reads the version of a dependency
     */
    private fun readVersion(versionInfo: JsonObject, token: String): String {
        var version: String? = null
        versionInfo["libraries"]!!.jsonArray.forEach { library ->
            val libraryObject = library.jsonObject
            val name = libraryObject["name"]!!.jsonPrimitive.content

            if (name.contains(token)) {
                version = name.substring(name.lastIndexOf(":") + 1, name.lastIndex + 1)
                return@forEach
            }
        }
        if (version == null) throw RuntimeException("Could not get version for dependency by token $token")

        return version!!
    }
}
