package com.redgrapefruit.openmodinstaller.util

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

object Hash {
    fun checksum(inPath: String): ByteArray {
        val input = File(inPath)

        try {
            FileInputStream(input).use { inputStream ->
                val digest = MessageDigest.getInstance("SHA-512")
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