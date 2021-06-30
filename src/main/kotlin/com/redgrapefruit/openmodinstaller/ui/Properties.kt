package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sun.security.auth.module.NTSystem

/**
 * The properties defined by the UI layer which can be exposed to the functional layer.
 */
object Properties {
    private val ntSystem = NTSystem()

    // Settings
    var modsFolderField by remember { mutableStateOf("C:/Users/${ntSystem.name}/AppData/Roaming/.minecraft/mods") }
    var cacheFolderField by remember { mutableStateOf("C:/Users/${ntSystem.name}/.openmodinstaller/cache") }
    var useUnverifiedSources by remember { mutableStateOf(true) }
}