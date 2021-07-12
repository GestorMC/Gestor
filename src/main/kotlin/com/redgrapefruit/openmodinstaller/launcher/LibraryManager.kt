package com.redgrapefruit.openmodinstaller.launcher

import com.redgrapefruit.openmodinstaller.task.downloadFile
import com.redgrapefruit.openmodinstaller.util.unjar
import kotlinx.serialization.json.*
import org.apache.commons.lang3.SystemUtils
import java.io.File

/**
 * Checks and installs Minecraft's libraries
 */
object LibraryManager {
    // Variants for natives names in different version.json formats
    private val NATIVES_WINDOWS_VARIANTS = listOf("natives_windows", "natives-windows")
    private val NATIVES_LINUX_VARIANTS = listOf("natives_linux", "natives-linux")
    private val NATIVES_OSX_VARIANTS = listOf("natives_macos", "natives_mac", "natives_osx", "natives-osx")

    /**
     * A [MutableList] of all natives
     */
    private val nativeLibraries: MutableList<String> = mutableListOf()

    /**
     * The path to the root directory of the game (for example AppData/Roaming/.minecraft on Windows)
     */
    private lateinit var gamePath: String

    /**
     * Checks all libraries and installs missing ones.
     *
     * Internal. Can be run via [SetupManager]
     */
    internal fun checkLibraries(
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
        nativesPath: String
    ) {

        // Save game path for later
        this.gamePath = gamePath

        // If the version info JSON inherits from another version info JSON,
        // check all the libraries from the parent version info JSON
        if (versionInfoObject.contains("inheritsFrom")) {
            val parentText = "{\n\t\n}"

            // Parse into JSON, then extract the JsonArray of libraries and perform the check
            val parentObject = Json.decodeFromString(JsonObject.serializer(), parentText)
            val parentLibrariesArray = parentObject["libraries"]!!
            checkAndDownload(parentLibrariesArray.jsonArray)
        }

        // Check the main libraries array
        checkAndDownload(librariesArray)

        // Delete current natives if they exist
        val nativesFile = File(nativesPath)
        if (nativesFile.exists()) nativesFile.deleteRecursively()

        // Unjar all natives
        nativeLibraries.forEach { native -> unjar(native, nativesPath) }
    }

    /**
     * Checks a separate [JsonArray] of Minecraft libraries and downloads some/all of them if necessary
     */
    private fun checkAndDownload(
        /**
         * The checked libraries [JsonArray]
         */
        librariesArray: JsonArray
    ): Unit = librariesArray.forEach { library ->

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
                val nativeObjectNames = when {
                    // Compat with multiple format versions
                    SystemUtils.IS_OS_WINDOWS -> NATIVES_WINDOWS_VARIANTS
                    SystemUtils.IS_OS_LINUX -> NATIVES_LINUX_VARIANTS
                    SystemUtils.IS_OS_MAC_OSX -> NATIVES_OSX_VARIANTS
                    else -> throw RuntimeException("App running not on Windows, Linux or MacOS. This is not allowed for Java Edition")
                }
                val nativeObjectName = getNativeKeyName(nativeObjectNames, downloadsObject)

                val nativePath = "$gamePath/libraries/$cut1/$cut2/$cut3-$nativeObjectName.jar"
                val nativeFile = File(nativePath)

                if (!nativeFile.exists()) {
                    // Missing native libraries still have to be installed
                    downloadFile(
                        input =
                            // /classifiers
                            downloadsObject["classifiers"]!!
                            // /classifiers/natives_XXX
                            .jsonObject[nativeObjectName]!!
                            // /classifiers/natives_XXX/url
                            .jsonObject["url"]!!.jsonPrimitive.content,
                        output = nativePath
                    )
                } else {
                    // Add to list of native libs
                    nativeLibraries += nativePath
                }
            }

            if (downloadsObject.contains("artifact")) {
                // Just download the lib
                downloadFile(
                    input =
                        // /artifact
                        downloadsObject["artifact"]!!
                        // /artifact/url
                        .jsonObject["url"]!!.jsonPrimitive.content,
                    output = libraryPath
                )
            }
        }
    }

    /**
     * Tries out multiple variant keys and returns the right one
     */
    private fun getNativeKeyName(
        /**
         * All possible variants
         */
        variants: List<String>,
        /**
         * A JSONObject for the downloads
         */
        downloadsObject: JsonObject
    ): String {

        val classifiersObject = downloadsObject["classifiers"]!!.jsonObject

        variants.forEach { variant ->
            if (classifiersObject.contains(variant)) return variant
        }

        return variants[variants.lastIndex]
    }
}
