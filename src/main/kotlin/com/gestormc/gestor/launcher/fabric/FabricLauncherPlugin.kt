package com.gestormc.gestor.launcher.fabric

import com.gestormc.gestor.launcher.LauncherPlugin
import com.gestormc.gestor.launcher.core.LibraryManager
import com.gestormc.gestor.launcher.core.SetupManager
import com.gestormc.gestor.util.plusAssign
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.FileInputStream

/**
 * A [LauncherPlugin] providing FabricMC mod support
 */
object FabricLauncherPlugin : LauncherPlugin {
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
        // Use the Fabric JAR as the launched Minecraft JAR and fix to use the correct main class
        // Load version info's
        val fabricVersionInfo: JsonObject
        val vanillaVersionInfo: JsonObject
        FileInputStream("$root/versions/$version-fabric/$version-fabric.json").use { stream ->
            fabricVersionInfo = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }
        FileInputStream("$root/versions/$version/$version.json").use { stream ->
            vanillaVersionInfo = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }

        return source
            .replace(
                "$root/versions/$version/$version-$jarTemplate.jar",
                "$root/versions/$version-fabric/$version-fabric.jar"
            )
            .replace(
                vanillaVersionInfo["mainClass"]!!.jsonPrimitive.content,
                fabricVersionInfo["mainClass"]!!.jsonPrimitive.content
            )
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
        // Load Fabric version info
        val versionInfoPath = "$root/versions/$version-fabric/$version-fabric.json"
        val versionInfoObject: JsonObject
        FileInputStream(versionInfoPath).use { stream ->
            versionInfoObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }
        // Add all libs from there
        val builder = StringBuilder(classpath)
        builder += LibraryManager.getLibrariesFormatted(root, versionInfoObject)

        return builder.toString()
    }

    override fun onSetupEnd(root: String, version: String, optInLegacyJava: Boolean) {
        // Launch additional setup tasks
        fabricSetupInstallerTask(root)
        fabricRunInstallerTask(root, version, optInLegacyJava)
        fabricMigrateJarTask(root, version)
        SetupManager.setupLibraries(root, "$version-fabric")
    }
}
