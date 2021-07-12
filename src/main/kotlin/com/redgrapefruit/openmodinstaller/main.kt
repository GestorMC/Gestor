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
    SetupManager.setupVersionInfo("C:/Users/karpo/AppData/Roaming/.minecraft", "1.17")
//    MaterialTheme {
//        renderHomepage()
//    }
}