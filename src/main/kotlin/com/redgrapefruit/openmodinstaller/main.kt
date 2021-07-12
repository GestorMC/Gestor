package com.redgrapefruit.openmodinstaller

import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import com.redgrapefruit.openmodinstaller.launcher.OpenLauncher
import com.redgrapefruit.openmodinstaller.launcher.SetupManager
import com.redgrapefruit.openmodinstaller.ui.renderHomepage
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
}

fun main() = Window(title = "OpenModInstaller") {
    val gamePath = "C:/Users/karpo/AppData/Roaming/.minecraft"

    val launcher = OpenLauncher.create()
    launcher.setup("1.17")

//    MaterialTheme {
//        renderHomepage()
//    }
}