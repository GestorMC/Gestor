package com.redgrapefruit.openmodinstaller

import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.v1.MenuBar
import com.redgrapefruit.openmodinstaller.ui.renderHomepage
import com.redgrapefruit.openmodinstaller.util.Settings
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
}

fun main() = Window(title = "OpenModInstaller") {
    MaterialTheme {
        renderHomepage()
    }
}