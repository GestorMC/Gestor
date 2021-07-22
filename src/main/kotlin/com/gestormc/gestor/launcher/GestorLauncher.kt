package com.gestormc.gestor.launcher

import com.gestormc.gestor.data.ManifestReleaseType
import com.gestormc.gestor.launcher.core.*
import com.gestormc.gestor.launcher.fabric.FabricLauncherPlugin
import com.gestormc.gestor.launcher.forge.ForgeLauncherPlugin
import com.gestormc.gestor.util.InternalAPI
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import com.sun.security.auth.module.NTSystem
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.commons.lang3.SystemUtils
import java.io.*

/**
 * The main class for launching the game
 */
class GestorLauncher private constructor(
    private val root: String,
    private val isServer: Boolean,
    private val plugins: MutableSet<LauncherPlugin> = mutableSetOf(),
    private val authentication: YggdrasilUserAuthentication? = null,
    private val jarTemplate: String = if (isServer) "server" else "client",
    private val data: LibraryData = LibraryData()
) {

    /**
     * Has the launcher setup been run yet.
     */
    private var isSetUp: Boolean = false

    init {
        plugins.forEach { plugin -> plugin.onCreate(root, isServer, authentication, jarTemplate) }
    }

    /**
     * Adds the [plugin] to the plugin list
     */
    private fun withPlugin(plugin: LauncherPlugin): GestorLauncher {
        if (!plugins.contains(plugin)) plugins += plugin
        plugins.forEach { plugin_ -> plugin_.onAddPlugin(plugin) }
        return this
    }

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
        optInLegacyJava: Boolean = false
    ) {

        plugins.forEach { plugin -> plugin.onSetupStart(root, version, optInLegacyJava) }

        launcherVersionInfoSetupTask(root, version)
        launcherSetupLibrariesTask(root, version, data)
        launcherSetupJarTask(root, version, isServer)
        launcherSetupJavaTask(optInLegacyJava)
        launcherSetupAssetsTask(root, version)

        // Make dirs
        File("$root/assets").mkdirs()

        isSetUp = true

        plugins.forEach { plugin -> plugin.onSetupEnd(root, version, optInLegacyJava) }
    }

    /**
     * Removes the current game instance
     */
    fun clear() {
        val rootFile = File(root)

        rootFile.deleteRecursively()
        rootFile.mkdir()

        plugins.forEach { plugin -> plugin.onClear(root) }
    }

    /**
     * Launches Minecraft
     */
    fun launch(
        /**
         * Opt in legacy AdoptOpenJRE 8 for older Minecraft versions (1.16 and lower)
         */
        optInLegacyJava: Boolean = false,
        /**
         * The username of the player
         */
        username: String,
        /**
         * Maximum process memory in megabytes
         */
        maxMemory: Int,
        /**
         * Extra JVM arguments
         */
        jvmArgs: String = "",
        /**
         * Launched Minecraft version
         */
        version: String,
        /**
         * The type of the launched Minecraft version.
         *
         * See [ManifestReleaseType] for more.
         */
        versionType: String = "release"
    ) {

        plugins.forEach { plugin ->
            plugin.onLaunchStart(
                root,
                optInLegacyJava,
                username,
                maxMemory,
                jvmArgs,
                version,
                versionType
            )
        }

        val versionInfoPath = "$root/versions/$version/$version.json"

        // Make sure the setup has been run
        if (!isSetUp) throw RuntimeException("Cannot launch the game since the setup hasn't been run yet")

        // Parse version info
        val versionInfoObject: JsonObject
        FileInputStream(versionInfoPath).use { stream ->
            versionInfoObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }

        // Generate arguments
        var arguments = if (versionInfoObject.contains("minecraftArguments")) {
            launcherLegacyArgumentsTask(
                raw = versionInfoObject["minecraftArguments"]!!.jsonPrimitive.content,
                root = root,
                version = version,
                assetsIndexName =
                if (versionInfoObject.contains("inheritsFrom") && !versionInfoObject.contains("assets"))
                    getParentObject(versionInfoObject, root)["assets"]!!.jsonPrimitive.content
                else
                    versionInfoObject["assets"]!!.jsonPrimitive.content
            )
        } else {
            launcherModernArgumentsTask(
                version = version,
                root = root,
                assetsIndexName =
                if (versionInfoObject.contains("inheritsFrom") && !versionInfoObject.contains("assets"))
                    getParentObject(versionInfoObject, root)["assets"]!!.jsonPrimitive.content
                else
                    versionInfoObject["assets"]!!.jsonPrimitive.content,
                username = username,
                auth = authentication,
                versionType = versionType
            )
        }
        plugins.forEach { plugin ->
            arguments = plugin.processGameArguments(
                arguments,
                root,
                optInLegacyJava,
                username,
                maxMemory,
                jvmArgs,
                version,
                versionType
            )
        }

        var jvmArguments = launcherJVMArgumentsTask(maxMemory.toString(), jvmArgs)
        plugins.forEach { plugin ->
            jvmArguments = plugin.processJvmArguments(
                jvmArguments,
                root,
                optInLegacyJava,
                username,
                maxMemory,
                jvmArgs,
                version,
                versionType
            )
        }

        // Create classpath
        val replacers = mutableMapOf<String, (String) -> String>()
        val exceptions = mutableSetOf<String>()

        plugins.forEach { plugin ->
            replacers.putAll(
                plugin.generateLibraryReplacers(
                    jvmArguments,
                    root,
                    optInLegacyJava,
                    username,
                    maxMemory,
                    jvmArgs,
                    version,
                    versionType
                )
            )
            exceptions.addAll(
                plugin.generateLibraryExceptions(
                    jvmArguments,
                    root,
                    optInLegacyJava,
                    username,
                    maxMemory,
                    jvmArgs,
                    version,
                    versionType
                )
            )
        }

        var classpath = ".;$root/versions/$version/$version-$jarTemplate.jar;${
            launcherLibraryFormatTask(root, versionInfoObject, replacers, exceptions, data)
        }".applyClasspathFixes()
        if (versionInfoObject.contains("inheritsFrom")) { // inheritance support. No replacers or exceptions are applied here
            classpath += launcherLibraryFormatTask(root, getParentObject(versionInfoObject, root), data = data).applyClasspathFixes()
        }
        plugins.forEach { plugin ->
            classpath = plugin.processClasspath(
                classpath,
                root,
                optInLegacyJava,
                username,
                maxMemory,
                jvmArgs,
                version,
                versionType
            )
        }

        // Obtain the main class and create the command that launches Minecraft
        val mainClass = versionInfoObject["mainClass"]!!.jsonPrimitive.content
        var command =
            "${findLocalJavaPath(optInLegacyJava)} $jvmArguments -classpath $classpath $mainClass ${if (isServer) "nogui" else ""} $arguments"

        plugins.forEach { plugin ->
            command = plugin.processCommand(
                command,
                root,
                optInLegacyJava,
                username,
                maxMemory,
                jvmArgs,
                version,
                versionType,
                jarTemplate
            )
        }

        // Launch the Minecraft process
        try {
            val process = Runtime.getRuntime().exec(command)
            observeProcessOutput(process)
            process.waitFor()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        plugins.forEach { plugin ->
            plugin.onLaunchEnd(
                root,
                optInLegacyJava,
                username,
                maxMemory,
                jvmArgs,
                version,
                versionType
            )
        }
    }

    /**
     * Observes the out and err outputs of a [Process]
     */
    private fun observeProcessOutput(
        /**
         * Observed [Process]
         */
        process: Process
    ) {

        /**
         * Observes some output
         */
        fun observe(
            /**
             * [InputStream] of the process
             */
            inputStream: InputStream,
            /**
             * Output [PrintStream]
             */
            printStream: PrintStream
        ) {

            try {
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    printStream.println(line)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        observe(process.inputStream, System.out)
        observe(process.errorStream, System.err)
    }

    companion object {
        // Setup functions

        /**
         * Creates a new instance of [GestorLauncher] for running vanilla Minecraft
         */
        fun vanilla(
            /**
             * Game's root folder. Win AppData by default
             */
            root: String = "C:/Users/${NTSystem().name}/AppData/Roaming/.minecraft",
            /**
             * Is the Minecraft launched a server
             */
            isServer: Boolean = false,
            /**
             * If `true`, bypasses auth checks and runs pirated Minecraft.
             *
             * Do **not** set this to `true` outside of testing!
             */
            testingLaunch: Boolean = false
        ): GestorLauncher {

            val auth = if (testingLaunch) null else launcherAuthenticateTask()
            auth?.logIn()
            return GestorLauncher(root, isServer, authentication = auth)
        }

        /**
         * Creates a new instance of [GestorLauncher] for running Minecraft with FabricMC mods
         */
        fun fabric(
            /**
             * Game's root folder. Win AppData by default
             */
            root: String,
            /**
             * Is the Minecraft launched a server
             */
            isServer: Boolean = false,
            /**
             * If `true`, bypasses auth checks and runs pirated Minecraft.
             *
             * Do **not** set this to `true` outside of testing!
             */
            testingLaunch: Boolean = false
        ): GestorLauncher {

            val auth = if (testingLaunch) null else launcherAuthenticateTask()
            auth?.logIn()
            return GestorLauncher(root, isServer, authentication = auth).withPlugin(FabricLauncherPlugin)
        }

        /**
         * Creates a new instance of [GestorLauncher] for running Minecraft with MinecraftForge mods
         */
        fun forge(
            /**
             * Game's root folder. Win AppData by default
             */
            root: String,
            /**
             * Is the Minecraft launched a server
             */
            isServer: Boolean = false,
            /**
             * If `true`, bypasses auth checks and runs pirated Minecraft.
             *
             * Do **not** set this to `true` outside of testing!
             */
            testingLaunch: Boolean = false
        ): GestorLauncher {

            val auth = if (testingLaunch) null else launcherAuthenticateTask()
            auth?.logIn()
            return GestorLauncher(root, isServer, authentication = auth).withPlugin(ForgeLauncherPlugin)
        }

        /**
         * Finds the path for the local Java installation.
         *
         * Can be used as a utility externally.
         */
        @InternalAPI
        fun findLocalJavaPath(
            /**
             * Opt in legacy Java 8 for older Minecraft versions
             */
            optInLegacyJava: Boolean
        ): String {

            // Get the root for the Java installation
            val root = if (optInLegacyJava) "./java/adoptopenjre8" else "./java/adoptopenjre16"
            val rootFile = File(root)

            // Get the only subfolder with the actual Java (also prevents unnecessary hardcoding)
            var subroot: File? = null
            rootFile.listFiles()!!.forEach { file ->
                subroot = file
            }
            if (subroot == null) throw RuntimeException("Couldn't find the subroot of the Java installation. The root is empty")

            var out = "${subroot!!.absolutePath}/bin/java.exe"

            if (SystemUtils.IS_OS_UNIX) {
                // Sometimes there are extra ./ sequences in paths generated, so remove them
                if (out.contains("./")) {
                    out = out.replace("./", "")
                }
                // Remove .exe
                out = out.replace(".exe", "")

                // Set executable permissions for the files in the bin folder
                File(out).parentFile.listFiles()!!.forEach { file ->
                    file.setExecutable(true)
                }
            }

            // Return the main Java executable in the binaries folder
            return out
        }

        /**
         * Returns the parent version info. Only call if has `inheritsFrom`
         */
        @InternalAPI
        fun getParentObject(versionInfoObject: JsonObject, root: String): JsonObject {
            val parent = versionInfoObject["inheritsFrom"]!!.jsonPrimitive.content
            val parentPath = "$root/versions/$parent/$parent.json"

            val parentObject: JsonObject
            if (!File(parentPath).exists()) {
                throw RuntimeException("Couldn't find parent version info $parent. Please install it")
            } else {
                // Parse
                FileInputStream(parentPath).use { stream ->
                    parentObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
                }
            }

            return parentObject
        }
    }

    data class AuthDetails(val username: String, val password: String, val token: String = "")
}

// Additional Unix classpath-fixing
fun String.applyClasspathFixes(): String {
    if (SystemUtils.IS_OS_UNIX) {
        replace(".;", "")
        replace(";", ":")
    }
    return this
}
