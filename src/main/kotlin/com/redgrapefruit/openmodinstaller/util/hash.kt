package com.redgrapefruit.openmodinstaller.util

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

enum class Hash(private val hashName: String) {
    // MD5 and SHA1 will be removed in further releases
    MD5("MD5"),
    SHA1("SHA1"),

    // SHA256 is supported, but not used
    SHA256("SHA-256"),
    SHA512("SHA-512");

    fun checksum(inPath: String): ByteArray {
        val input = File(inPath)

        try {
            FileInputStream(input).use { inputStream ->
                val digest = MessageDigest.getInstance(hashName)
                val block = ByteArray(4096)
                var length: Int
                while (inputStream.read(block).also { length = it } > 0) {
                    digest.update(block, 0, length)
                }
                return digest.digest()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        throw RuntimeException("Couldn't obtain checksum of file")
    }
}