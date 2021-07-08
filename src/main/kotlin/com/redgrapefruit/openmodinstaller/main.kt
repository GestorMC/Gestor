package com.redgrapefruit.openmodinstaller

import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.IntSize
import com.redgrapefruit.openmodinstaller.ui.renderHomepage
import com.redgrapefruit.openmodinstaller.util.Settings
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
}

fun main() {
//    // Init discovery and settings
//    ModDiscovery.load(Settings.cacheFolderField)
//    Settings.load()
    // Launch Window
    Window(size = IntSize(1440, 1024), onDismissRequest = Settings::save) {
        MaterialTheme {
            renderHomepage()
        }
    }
}