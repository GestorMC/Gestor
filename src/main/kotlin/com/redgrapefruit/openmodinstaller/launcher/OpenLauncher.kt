package com.redgrapefruit.openmodinstaller.launcher

import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import com.redgrapefruit.openmodinstaller.launcher.core.*
import com.redgrapefruit.openmodinstaller.launcher.fabric.FabricLauncherPlugin
import com.redgrapefruit.openmodinstaller.util.InternalAPI
import com.sun.security.auth.module.NTSystem
import kotlinx.serialization.json.*
import java.io.*

/**
 * The main class for launching the game
 */
class OpenLauncher private constructor(
    private val root: String,
    private val isServer: Boolean,
    private val plugins: MutableSet<LauncherPlugin> = mutableSetOf(),
    private val authentication: YggdrasilUserAuthentication? = null,
    private val jarTemplate: String = if (isServer) "server" else "client") {

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
    fun withPlugin(plugin: LauncherPlugin): OpenLauncher {
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
        optInLegacyJava: Boolean = false) {

        plugins.forEach { plugin -> plugin.onSetupStart(root, version, optInLegacyJava) }

        SetupManager.setupVersionInfo(root, version)
        SetupManager.setupLibraries(root, version)
        SetupManager.setupJAR(root, version, isServer)
        SetupManager.setupJava(optInLegacyJava)
        SetupManager.setupAssets(root, version)

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
        versionType: String = "release") {

        plugins.forEach { plugin -> plugin.onLaunchStart(root, optInLegacyJava, username, maxMemory, jvmArgs, version, versionType) }

        val versionInfoPath = "$root/versions/$version/$version.json"

        // Make sure the setup has been run
        if (!isSetUp) throw RuntimeException("Cannot launch the game since the setup hasn't been run yet")

        // Parse version info
        val versionInfoObject: JsonObject
        FileInputStream(versionInfoPath).use { stream ->
            versionInfoObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())
        }

        // Generate arguments
        val arguments = if (versionInfoObject.contains("minecraftArguments")) {
            ArgumentManager.generateLegacyArguments(
                raw = versionInfoObject["minecraftArguments"]!!.jsonPrimitive.content,
                root = root,
                version = version,
                assetsIndexName =
                if (versionInfoObject.contains("inheritsFrom") && !versionInfoObject.contains("assets"))
                    getParentObject(versionInfoObject, root)["assets"]!!.jsonPrimitive.content
                else
                    versionInfoObject["assets"]!!.jsonPrimitive.content)
        } else {
            ArgumentManager.generateModernArguments(
                version = version,
                root = root,
                assetsIndexName =
                if (versionInfoObject.contains("inheritsFrom") && !versionInfoObject.contains("assets"))
                    getParentObject(versionInfoObject, root)["assets"]!!.jsonPrimitive.content
                else
                    versionInfoObject["assets"]!!.jsonPrimitive.content,
                username = username,
                auth = authentication,
                versionType = versionType)
        }
        plugins.forEach { plugin -> plugin.onArgumentCreation(arguments, root, optInLegacyJava, username, maxMemory, jvmArgs, version, versionType) }

        val jvmArguments = ArgumentManager.generateJVMArguments(maxMemory.toString(), jvmArgs)
        plugins.forEach { plugin -> plugin.onJvmArgumentCreation(jvmArguments, root, optInLegacyJava, username, maxMemory, jvmArgs, version, versionType) }

        // Create classpath
        var classpath = ".;$root/versions/$version/$version-$jarTemplate.jar;${LibraryManager.getLibrariesFormatted(root, versionInfoObject)}"
        if (versionInfoObject.contains("inheritsFrom")) { // inheritance support
            classpath += LibraryManager.getLibrariesFormatted(root, getParentObject(versionInfoObject, root))
        }
        plugins.forEach { plugin ->
            classpath = plugin.processClasspath(classpath, root, optInLegacyJava, username, maxMemory, jvmArgs, version, versionType)
        }

        // Obtain the main class and create the command that launches Minecraft
        val mainClass = versionInfoObject["mainClass"]!!.jsonPrimitive.content
        var command = "${findLocalJavaPath(optInLegacyJava)} $jvmArguments -classpath $classpath $mainClass ${if (isServer) "nogui" else ""} $arguments"

        plugins.forEach { plugin -> command = plugin.processCommand(command, root, optInLegacyJava, username, maxMemory, jvmArgs, version, versionType, jarTemplate) }

        println(command)

        // Launch the Minecraft process
        try {
            val process = Runtime.getRuntime().exec(command)
            observeProcessOutput(process)
            process.waitFor()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        plugins.forEach { plugin -> plugin.onLaunchEnd(root, optInLegacyJava, username, maxMemory, jvmArgs, version, versionType) }
    }

    /**
     * Observes the out and err outputs of a [Process]
     */
    private fun observeProcessOutput(
        /**
         * Observed [Process]
         */
        process: Process) {

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
            printStream: PrintStream) {

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
         * Creates a new instance of [OpenLauncher] for running vanilla Minecraft
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
            testingLaunch: Boolean = false)

        : OpenLauncher {
            val auth = if (testingLaunch) null else AuthManager.start()
            auth?.logIn()
            return OpenLauncher(root, isServer, authentication = auth)
        }

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
            testingLaunch: Boolean = false): OpenLauncher {

            val auth = if (testingLaunch) null else AuthManager.start()
            auth?.logIn()
            return OpenLauncher(root, isServer, authentication = auth).withPlugin(FabricLauncherPlugin)
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
            optInLegacyJava: Boolean): String {

            // Get the root for the Java installation
            val root = if (optInLegacyJava) "./java/adoptopenjre8" else "./java/adoptopenjre16"
            val rootFile = File(root)

            // Get the only subfolder with the actual Java (also prevents unnecessary hardcoding)
            var subroot: File? = null
            rootFile.listFiles()!!.forEach { file ->
                subroot = file
            }
            if (subroot == null) throw RuntimeException("Couldn't find the subroot of the Java installation. The root is empty")

            // Return the main Java executable in the binaries folder
            return "${subroot!!.absolutePath}/bin/java.exe"
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
