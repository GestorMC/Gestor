package com.redgrapefruit.openmodinstaller.launcher

import com.mcgoodtime.gjmlc.core.JavaArgumentHack
import com.mojang.authlib.UserType
import com.sun.security.auth.module.NTSystem
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.io.FileInputStream

/**
 * The main class for launching the game
 */
class OpenLauncher private constructor(private val root: String) {
    /**
     * Sets up a new game instance.
     *
     * If making from scratch, run [clear] beforehand
     */
    fun setup(
        /**
         * Minecraft version
         */
        version: String,
        /**
         * Opt in legacy AdoptOpenJRE 8
         */
        optInLegacyJava: Boolean = false) {

        SetupManager.setupVersionInfo(root, version)
        SetupManager.setupLibraries(root, version)
        SetupManager.setupJAR(root, version)
        SetupManager.setupJava(optInLegacyJava)

        // Make dirs
        File("$root/assets").mkdirs()

    }

    /**
     * Removes the current game instance
     */
    fun clear() {
        val rootFile = File(root)

        rootFile.deleteRecursively()
        rootFile.mkdir()
    }

    fun launch(username: String, maxMemory: Int, jvmArgs: String = "", version: String) {
        val versionInfoPath = "$root/versions/$version/$version.json"

        // Make sure version info is set up
        if (!File(versionInfoPath).exists()) SetupManager.setupVersionInfo(root, version)

        // Parse version info
        val versionInfoObject: JsonObject
        FileInputStream(versionInfoPath).use { stream ->
            versionInfoObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }

        // Check if the legacy argument format is used
        val arguments = if (versionInfoObject.contains("minecraftArguments")) {
            generateLegacyArguments(
                raw = versionInfoObject["minecraftArguments"]!!.jsonPrimitive.content,
                version = version,
                assetsIndexName = versionInfoObject["assets"]!!.jsonPrimitive.content)
        } else {
            generateModernArguments()
        }
    }

    private fun generateLegacyArguments(raw: String, version: String, assetsIndexName: String): String {
        return raw
            .replace(JavaArgumentHack.VERSION_NAME, version)
            .replace(JavaArgumentHack.GAME_DIRECTORY, root)
            .replace(JavaArgumentHack.ASSETS_ROOT, "$root/assets")
            .replace(JavaArgumentHack.ASSETS_INDEX_NAME, assetsIndexName)
            .replace(JavaArgumentHack.AUTH_UUID, "auth_uuid")
            .replace(JavaArgumentHack.AUTH_ACCESS_TOKEN, "auth_access_token")
            .replace(JavaArgumentHack.USER_PROPERTIES, "{}")
            .replace(JavaArgumentHack.USER_TYPE, "legacy")
    }

    private fun generateModernArguments(): String = ""

    companion object {
        /**
         * Creates a new instance of [OpenLauncher]
         */
        fun create(
            /**
             * Game's root folder. Win AppData by default
             */
            root: String = "C:/Users/${NTSystem().name}/AppData/Roaming/.minecraft")

        : OpenLauncher = OpenLauncher(root)
    }
}
