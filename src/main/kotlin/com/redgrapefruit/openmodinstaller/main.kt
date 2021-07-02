package com.redgrapefruit.openmodinstaller

import androidx.compose.desktop.Window
import androidx.compose.ui.unit.IntSize
import com.redgrapefruit.openmodinstaller.core.ModJSONDiscovery
import com.redgrapefruit.openmodinstaller.util.Settings
import com.redgrapefruit.openmodinstaller.ui.createUI
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
}

fun main() {
    // Init discovery and settings
    ModJSONDiscovery.load(Settings.cacheFolderField)
    Settings.load()
    // Launch Window
    Window(resizable = false, size = IntSize(850, 650), onDismissRequest = { Settings.save() }) {
        createUI()
    }
}