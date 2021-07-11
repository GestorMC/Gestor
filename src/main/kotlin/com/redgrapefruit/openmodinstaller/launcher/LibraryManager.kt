package com.redgrapefruit.openmodinstaller.launcher

import kotlinx.serialization.json.*
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.lang.RuntimeException

/**
 * Checks and installs Minecraft's libraries
 */
object LibraryManager {
    /**
     * A [MutableList] of every current missing library's path
     */
    private val missingLibraries: MutableList<String> = mutableListOf()

    /**
     * A [MutableList] of all natives
     */
    private val nativeLibraries: MutableList<String> = mutableListOf()

    /**
     * The path to the root directory of the game (for example AppData/Roaming/.minecraft on Windows)
     */
    private lateinit var gamePath: String

    /**
     * Checks all libraries and installs missing ones
     */
    fun checkLibraries(
        /**
         * The path to the game folder root
         */
        gamePath: String,
        /**
         * A root [JsonObject] for the Mojang version info JSON
         */
        versionInfoObject: JsonObject,
        /**
         * A [JsonArray] of libraries for the game
         */
        librariesArray: JsonArray,
        /**
         * The path to natives of the game
         */
        nativesPath: String) {

        // Save game path for later
        this.gamePath = gamePath

        // If the version info JSON inherits from another version info JSON,
        // check all the libraries from the parent version info JSON
        if (versionInfoObject.contains("inheritsFrom")) {
            // TODO: Implement downloading (redgrapefruit09)
            val parentText = "{\n\t\n}"

            // Parse into JSON, then extract the JsonArray of libraries and perform the check
            val parentObject = Json.decodeFromString(JsonObject.serializer(), parentText)
            val parentLibrariesArray = parentObject["libraries"]!!
            check(parentLibrariesArray.jsonArray)
        }

        // Check the main libraries array
        check(librariesArray)

        // Download all missing libs


        // Delete current natives if they exist
        val nativesFile = File(nativesPath)
        if (nativesFile.exists()) nativesFile.deleteRecursively()



        // Unzip all natives
        nativeLibraries.forEach { native -> unzip(native, nativesPath) }


    }

    /**
     * Checks a separate [JsonArray] of Minecraft libraries
     */
    private fun check(
        /**
         * The checked libraries [JsonArray]
         */
        librariesArray: JsonArray): Unit = librariesArray.forEach { library ->

        // Extract the JsonObject
        val libraryObject = library.jsonObject

        // Get the name and cut it into pieces for making an absolute path for the library
        val name = libraryObject["name"]!!.jsonPrimitive.content

        val cut1 = name
            .substring(0, name.lastIndexOf(":") + 1)
            .replace(".", "/")
            .replace(":", "/")
        val cut2 = name
            .substring(name.lastIndexOf(":"))
        val cut3 = name
            .substring(name.indexOf(":") + 1)
            .replace(":", "-")

        // Join the pieces together into an absolute path
        val libraryPath = "$gamePath/libraries/$cut1/$cut2/$cut3.jar"

        // Check if the library doesn't exist
        val libraryFile = File(libraryPath)
        if (!libraryFile.exists()) {
            // Group into natives or missing
            // If has classifiers -> native
            // If has artifact -> regular
            // Else isn't used since some libraries have both classifiers and artifact (they have both regular and native versions)

            val downloadsObject = libraryObject["downloads"]!!.jsonObject

            if (downloadsObject.contains("classifiers")) {
                val nativeObjectName = when {
                    SystemUtils.IS_OS_WINDOWS -> "natives_windows"
                    SystemUtils.IS_OS_LINUX -> "natives_linux"
                    SystemUtils.IS_OS_MAC_OSX -> "macos"
                    else -> throw RuntimeException("App running not on Windows, Linux or MacOS")
                }

                val nativePath = "$gamePath/libraries/$cut1/$cut2/$cut3-$nativeObjectName.jar"
                val nativeFile = File(nativePath)

                if (!nativeFile.exists()) {
                    // Missing native libraries still have to be installed
                    missingLibraries += name
                } else {
                    nativeLibraries += name
                }
            }

            if (downloadsObject.contains("manifest")) {
                nativeLibraries += name
            }
        }
    }

    /**
     * Unzips files
     */
    private fun unzip(
        /**
         * Path to the input ZIP file
         */
        input: String,
        /**
         * Path to the output directory
         */
        output: String) {


    }
}
