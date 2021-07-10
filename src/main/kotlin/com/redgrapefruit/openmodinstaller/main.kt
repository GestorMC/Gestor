package com.redgrapefruit.openmodinstaller

import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import com.redgrapefruit.openmodinstaller.ui.Dashboard
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
}

fun main() = Window(title = "OpenModInstaller") {
    MaterialTheme {
        DesktopTheme {
            Dashboard()
        }
    }
}