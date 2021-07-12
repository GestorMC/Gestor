package com.redgrapefruit.openmodinstaller

import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import com.redgrapefruit.openmodinstaller.launcher.SetupManager
import com.redgrapefruit.openmodinstaller.ui.renderHomepage
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
}

fun main() = Window(title = "OpenModInstaller") {
    val gamePath = "C:/Users/karpo/AppData/Roaming/.minecraft"

    SetupManager.setupVersionInfo(gamePath, "1.17")
    SetupManager.setupJAR(gamePath, "1.17")
    SetupManager.setupLibraries(gamePath, "1.17")
    SetupManager.setupJava()
//    MaterialTheme {
//        renderHomepage()
//    }
}