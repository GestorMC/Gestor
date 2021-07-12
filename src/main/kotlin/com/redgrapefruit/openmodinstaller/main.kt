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

fun main() {
    val launcher = OpenLauncher.create()
    launcher.clear()
    launcher.setup("1.17")
    launcher.launch("Player432", 2000, "", "1.17", "auth_uuid", "auth_access_token", "release")
}