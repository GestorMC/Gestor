package com.gestormc.gestor.launcher

import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication

/**
 * A [LauncherPlugin] provides more capabilities for the user to extend the [OpenLauncher] with his/her own functionality.
 *
 * All [LauncherPlugin] methods are optional to implement, so you can stick with just some of them.
 *
 * Applying a [LauncherPlugin] is quite easy, create a helper method like `OpenLauncher.vanilla` and do this:
 *
 * ```kotlin
 * return OpenLauncher(...).withPlugin(MyLauncherPlugin)
 * ```
 */
interface LauncherPlugin {
    /**
     * An event called when an instance of the [OpenLauncher] is created
     */
    fun onCreate(root: String, isServer: Boolean, authentication: YggdrasilUserAuthentication?, jarTemplate: String) =
        Unit

    /**
     * An event called when a new plugin is added to the [OpenLauncher]
     */
    fun onAddPlugin(plugin: LauncherPlugin) = Unit

    /**
     * An event called when the setup starts
     */
    fun onSetupStart(root: String, version: String, optInLegacyJava: Boolean) = Unit

    /**
     * An event called when the setup ends
     */
    fun onSetupEnd(root: String, version: String, optInLegacyJava: Boolean) = Unit

    /**
     * An event called when an environment is cleared by the [OpenLauncher]
     */
    fun onClear(root: String) = Unit

    /**
     * An event called when the launch operation starts
     */
    fun onLaunchStart(
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String
    ) = Unit

    /**
     * A hook allowing you to modify the game arguments in any way
     */
    fun processGameArguments(
        arguments: String,
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String
    ): String = arguments

    /**
     * A hook allowing you to modify the JVM arguments in any way
     */
    fun processJvmArguments(
        jvmArguments: String,
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String
    ): String = jvmArguments

    /**
     * Allows you to generate library replacers.
     *
     * These are applied when the **main** classpath part is generated, for custom parts, you can use [processClasspath]
     */
    fun generateLibraryReplacers(
        jvmArguments: String,
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String
    ): Map<String, (String) -> String> = emptyMap()

    /**
     * Allows you to generate library exceptions.
     *
     * These are applied when the **main** classpath part is generated, for custom parts, you can use [processClasspath]
     */
    fun generateLibraryExceptions(
        jvmArguments: String,
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String
    ): Set<String> = emptySet()

    /**
     * An event called after the full classpath is generated.
     */
    fun processClasspath(
        classpath: String,
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String
    ): String = classpath

    /**
     * An extra hook allowing you to do additional processing on the Minecraft launch command.
     */
    fun processCommand(
        source: String,
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String,
        jarTemplate: String
    ): String = source

    /**
     * An event called when the launch operation ends.
     */
    fun onLaunchEnd(
        root: String,
        optInLegacyJava: Boolean,
        username: String,
        maxMemory: Int,
        jvmArgs: String,
        version: String,
        versionType: String
    ) = Unit
}
