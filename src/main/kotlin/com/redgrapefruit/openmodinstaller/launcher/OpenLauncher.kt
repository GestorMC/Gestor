package com.redgrapefruit.openmodinstaller.launcher

import com.sun.security.auth.module.NTSystem
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.*

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

    fun launch(
        username: String,
        maxMemory: Int,
        jvmArgs: String = "",
        version: String,
        authUuid: String,
        authAccessToken: String,
        versionType: String = "release") {

        val versionInfoPath = "$root/versions/$version/$version.json"

        // Make sure version info is set up
        if (!File(versionInfoPath).exists()) SetupManager.setupVersionInfo(root, version)

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
                assetsIndexName = versionInfoObject["assets"]!!.jsonPrimitive.content,
                authUuid = authUuid,
                authAccessToken = authAccessToken,
                maxMemory = maxMemory,
                jvmArgs = jvmArgs)
        } else {
            ArgumentManager.generateModernArguments(
                version = version,
                root = root,
                assetsIndexName = versionInfoObject["assets"]!!.jsonPrimitive.content,
                username = username,
                authUuid = authUuid,
                authAccessToken = authAccessToken,
                versionType = versionType,
                maxMemory = maxMemory,
                jvmArgs = jvmArgs)
        }

        // Obtain the main class and create the command that launches Minecraft
        val mainClass = versionInfoObject["mainClass"]!!.jsonPrimitive.content
        //val command = "java -classpath ${LibraryManager.getLibrariesFormatted(root, versionInfoObject)}\"\"$root/versions/$version/$version.jar\" $mainClass $arguments"
        val javaFile = File("./java/adoptopenjre16/jdk-16.0.1+9-jre/bin/java.exe")

        val command = "${javaFile.absolutePath} -classpath .;$root/versions/$version/$version.jar;${LibraryManager.getLibrariesFormatted(root, versionInfoObject)} $mainClass $arguments"

        println(command)

        // Launch the Minecraft process
        try {
            val process = Runtime.getRuntime().exec(command)
            observeProcessOutput(process)
            process.waitFor()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
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
