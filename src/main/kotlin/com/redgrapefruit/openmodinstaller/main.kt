package com.redgrapefruit.openmodinstaller

import com.redgrapefruit.openmodinstaller.launcher.OpenLauncher
import kotlinx.serialization.json.Json

val JSON = Json {
    ignoreUnknownKeys = true
}

fun main() {
    val launcher = OpenLauncher.create()
    launcher.clear()
    launcher.setup("1.17")
    launcher.launch(false, "Player432", 2000, "", "1.17", "auth_uuid", "auth_access_token", "release")
}