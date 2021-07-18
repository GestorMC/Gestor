package com.gestormc.gestor.launcher.core

import com.gestormc.gestor.const.AUTH_SERVICE
import com.gestormc.gestor.launcher.GestorLauncher
import com.mojang.authlib.Agent
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import kotlinx.serialization.json.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

/**
 * Performs all authentication operations.
 */
object AuthManager {
    /**
     * Authenticates the user.
     *
     * All credentials are secured using the Base64 encryption algorithm.
     */
    internal fun start(): YggdrasilUserAuthentication {
        val auth: YggdrasilUserAuthentication

        // Try load credentials
        val credentialFile = File("./auth/credentials.json")
        if (!credentialFile.exists()) {
            credentialFile.mkdirs()
            credentialFile.createNewFile()

            // Write defaults
            val details = demandDetails()
            FileOutputStream(credentialFile).use { stream ->
                stream.write(Json.encodeToString(JsonObject.serializer(), encode(details)).encodeToByteArray())
            }

            // Authenticate
            auth = YggdrasilUserAuthentication(AUTH_SERVICE, "", Agent.MINECRAFT)
            auth.setPassword(details.password)
            auth.setUsername(details.username)

        } else {
            // Load credentials
            val details = decode(credentialFile)

            // Authenticate
            auth = YggdrasilUserAuthentication(AUTH_SERVICE, details.token, Agent.MINECRAFT)
            auth.setPassword(details.password)
            auth.setUsername(details.username)
        }

        return auth
    }

    /**
     * Encodes the [details] into a [JsonObject]
     */
    private fun encode(details: GestorLauncher.AuthDetails): JsonObject {
        val encodedUsername = Base64.getEncoder().encode(details.username.encodeToByteArray()).decodeToString()
        val encodedPassword = Base64.getEncoder().encode(details.password.encodeToByteArray()).decodeToString()
        val encodedToken = Base64.getEncoder().encode("".encodeToByteArray()).decodeToString()

        return buildJsonObject {
            put("username", JsonPrimitive(encodedUsername))
            put("password", JsonPrimitive(encodedPassword))
            put("token", JsonPrimitive(encodedToken))
        }
    }

    /**
     * Decodes the [GestorLauncher.AuthDetails] from the [credentialFile]
     */
    private fun decode(credentialFile: File): GestorLauncher.AuthDetails {
        val username: String
        val password: String
        val token: String

        FileInputStream(credentialFile).use { stream ->
            val jsonObject = Json.decodeFromString(JsonObject.serializer(), stream.readBytes().decodeToString())

            val encodedUsername = jsonObject["username"]!!.jsonPrimitive.content
            val encodedPassword = jsonObject["password"]!!.jsonPrimitive.content
            val encodedToken = jsonObject["token"]!!.jsonPrimitive.content

            username = Base64.getDecoder().decode(encodedUsername.encodeToByteArray()).decodeToString()
            password = Base64.getDecoder().decode(encodedPassword.encodeToByteArray()).decodeToString()
            token = Base64.getDecoder().decode(encodedToken.encodeToByteArray()).decodeToString()
        }

        return GestorLauncher.AuthDetails(username, password, token)
    }

    /**
     * Demands the auth details from the user
     */
    private fun demandDetails(): GestorLauncher.AuthDetails {
        // TODO: Display a window demanding the user to enter the details
        return GestorLauncher.AuthDetails("", "")
    }
}
