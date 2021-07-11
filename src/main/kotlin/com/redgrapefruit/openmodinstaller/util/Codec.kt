package com.redgrapefruit.openmodinstaller.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Cache codec format.
 *
 * No direct access to [Codec] instances can be made.
 *
 * For that, please use methods in the [CodecManager].
 */
@Serializable
internal data class Codec(
    /**
     * List of cache indexed names
     */
    val indexes: List<String>,
    /**
     * List of URLs where the cache was downloaded from
     */
    val urls: List<String>
)

/**
 * Easy [Codec] management utils
 */
object CodecManager {
    /**
     * Writes an entry to the appropriate codec for [cacheFolderPath] with [index] and [url]
     */
    fun addEntry(cacheFolderPath: String, index: String, url: String) {
        val codec = getOrCreate(cacheFolderPath)

        // Add
        val indexes = mutate(codec.indexes) { this += index }
        val urls = mutate(codec.urls) { this += url }

        // Rewrite
        rewrite(cacheFolderPath, Codec(indexes, urls))
    }

    /**
     * Removes an entry from the appropriate codec for [cacheFolderPath] with [index] and [url].
     *
     * If such codec entry does not exist, the operation is ignored and no error is thrown.
     */
    fun removeEntry(cacheFolderPath: String, index: String, url: String) {
        val codec = getOrCreate(cacheFolderPath)

        // Remove
        val indexes = mutate(codec.indexes) { this -= index }
        val urls = mutate(codec.indexes) { this -= url }

        // Rewrite
        rewrite(cacheFolderPath, Codec(indexes, urls))
    }

    /**
     * Checks if the appropriate codec for [cacheFolderPath] contains entry with [url]
     */
    fun hasEntry(cacheFolderPath: String, url: String): Boolean {
        val codec = getOrCreate(cacheFolderPath)

        return codec.urls.contains(url)
    }

    /**
     * Returns an entry from the appropriate codec for [cacheFolderPath] with [url].
     *
     * If entry not found, throws [RuntimeException]
     */
    fun getEntry(cacheFolderPath: String, url: String): String {
        val codec = getOrCreate(cacheFolderPath)

        codec.indexes.forEach { index ->
            codec.urls.forEach { foundUrl ->
                if (foundUrl == url) {
                    return index
                }
            }
        }

        throw RuntimeException("Couldn't find entry. Please run hasEntry beforehand to debug the issue")
    }

    /**
     * Clears the entire appropriate codec for [cacheFolderPath]
     */
    fun clear(cacheFolderPath: String) {
        val codec = getOrCreate(cacheFolderPath)

        // Rewrite with empty lists
        rewrite(cacheFolderPath, Codec(listOf(), listOf()))
    }

    /**
     * Gets the appropriate codec for [cacheFolderPath] or creates it with default values
     */
    private fun getOrCreate(cacheFolderPath: String): Codec {
        val codecPath = "$cacheFolderPath/codec.json"
        val codecFile = File(codecPath)

        // Create codec if not exists
        if (!codecFile.exists()) {
            codecFile.createNewFile()

            val default = Codec(listOf(), listOf())
            val out = Json.encodeToString(Codec.serializer(), default)

            FileOutputStream(codecFile).use { stream ->
                stream.write(out.encodeToByteArray())
            }

            return default
        }

        // Here if exists
        // Read codec
        val codec: Codec
        FileInputStream(codecFile).use { stream ->
            codec = Json.decodeFromString(Codec.serializer(), stream.readBytes().decodeToString())
        }
        return codec
    }

    /**
     * Rewrites an appropriate codec from [cacheFolderPath] with a new [codec]
     */
    private fun rewrite(cacheFolderPath: String, codec: Codec) {
        val codecPath = "$cacheFolderPath/codec.json"
        val codecFile = File(codecPath)

        codecFile.delete()
        codecFile.createNewFile()

        val out = Json.encodeToString(Codec.serializer(), codec)

        FileOutputStream(codecFile).use { stream ->
            stream.write(out.encodeToByteArray())
        }
    }

    /**
     * Allows you to mutate (perform [MutableList] operations) an immutable [List].
     *
     * It's not the most performant or efficient solution to this problem, but it does the job for
     * the scope of this application.
     *
     * Example:
     * ```kotlin
     * val immutable = listOf(1, 2, 3, 4)
     *
     * immutable = mutate {
     *     add(4)
     *     add(5)
     * }
     * ```
     */
    @OptIn(ExperimentalStdlibApi::class)
    private inline fun <T> mutate(immutable: List<T>, operation: MutableList<T>.() -> Unit): List<T> {
        // List -> MutableList
        val mutable = mutableListOf<T>().apply { addAll(immutable) }
        // Invoke operation
        operation.invoke(mutable)
        // MutableList -> List
        return buildList { addAll(mutable) }
    }
}
