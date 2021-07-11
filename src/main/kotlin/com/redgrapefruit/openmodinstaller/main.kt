package com.redgrapefruit.openmodinstaller

import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import com.redgrapefruit.openmodinstaller.ui.Dashboard
import com.redgrapefruit.openmodinstaller.ui.lightColors
import kotlinx.coroutines.runBlocking

import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
}

fun main() = runBlocking {
    Window(title = "OpenModInstaller") {
        MaterialTheme(colors = lightColors) {
            DesktopTheme {
                Dashboard()
            }
        }
    }
}