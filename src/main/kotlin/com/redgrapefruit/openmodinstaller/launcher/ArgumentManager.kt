package com.redgrapefruit.openmodinstaller.launcher

import com.mcgoodtime.gjmlc.core.JavaArgumentHack
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import com.redgrapefruit.openmodinstaller.data.ManifestReleaseType
import com.redgrapefruit.openmodinstaller.util.plusAssign
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.apache.commons.lang3.SystemUtils
import java.io.FileInputStream

/**
 * Manages Minecraft process execution arguments
 */
object ArgumentManager {
    private const val LAUNCHER_BRAND = "OpenLauncher"

    /**
     * Generates launch arguments using the legacy format
     */
    internal fun generateLegacyArguments(
        /**
         * Raw string from the format
         */
        raw: String,
        /**
         * Root game directory
         */
        root: String,
        /**
         * Launched Minecraft version
         */
        version: String,
        /**
         * The version of the asset index
         */
        assetsIndexName: String,
        /**
         * The [YggdrasilUserAuthentication]
         */
        auth: YggdrasilUserAuthentication? = null): String {

        // The legacy format acts as a template, and all we have to do here is replace the placeholders with actual values
        return raw
            .replace(JavaArgumentHack.VERSION_NAME, version)
            .replace(JavaArgumentHack.GAME_DIRECTORY, root)
            .replace(JavaArgumentHack.ASSETS_ROOT, "$root/assets")
            .replace(JavaArgumentHack.ASSETS_INDEX_NAME, assetsIndexName)
            .replace(JavaArgumentHack.AUTH_UUID, if (auth == null) "access_uuid" else auth.userID)
            .replace(JavaArgumentHack.AUTH_ACCESS_TOKEN, if (auth == null) "access_token" else auth.authenticatedToken)
            .replace(JavaArgumentHack.USER_PROPERTIES, "{}")
            .replace(JavaArgumentHack.USER_TYPE, "mojang")
    }

    /**
     * Generates launch arguments using the modern format
     */
    internal fun generateModernArguments(
        /**
         * The launched Minecraft version
         */
        version: String,
        /**
         * The root game directory
         */
        root: String,
        /**
         * The version of the asset index
         */
        assetsIndexName: String,
        /**
         * The username of the player
         */
        username: String,
        /**
         * The [YggdrasilUserAuthentication]
         */
        auth: YggdrasilUserAuthentication? = null,
        /**
         * The launched version type.
         *
         * Corresponds to the string values of the [ManifestReleaseType] enum
         */
        versionType: String = "release"): String {

        val builder = StringBuilder()

        // Arguments
        builder += " --username $username"
        builder += " --version $version"
        builder += " --gameDir $root"
        builder += " --assetsDir $root/assets"
        builder += " --assetIndex $assetsIndexName"
        builder += " --uuid ${if (auth == null) "access_uuid" else auth.userID}"
        builder += " --accessToken ${if (auth == null) "access_token" else auth.authenticatedToken}"
        builder += " --userType mojang"
        builder += " --versionType $versionType"

        return builder.toString()
    }

    /**
     * Generates special options for the JVM when launching Minecraft
     */
    internal fun generateJVMArguments(
        /**
         * Maximum process memory in megabytes
         */
        maxMemory: String,
        /**
         * Additional JVM arguments
         */
        jvmArgs: String): String {

        val builder = StringBuilder()

        builder += "-Xmx${maxMemory}M"
        if (jvmArgs.isNotEmpty()) builder += " $jvmArgs"
        if (SystemUtils.IS_OS_MAC_OSX) {
            builder += " -XstartOnFirstThread"
        }
        if (SystemUtils.OS_ARCH == "x86") {
            builder += " -Xss1M"
        }
        builder += " -Dminecraft.launcher.brand=$LAUNCHER_BRAND"

        return builder.toString()
    }
}
