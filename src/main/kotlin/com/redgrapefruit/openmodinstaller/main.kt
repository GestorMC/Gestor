package com.redgrapefruit.openmodinstaller

import androidx.compose.desktop.Window
import androidx.compose.ui.unit.IntSize
import com.redgrapefruit.openmodinstaller.core.ModJSONDiscovery
import com.redgrapefruit.openmodinstaller.ui.Properties
import com.redgrapefruit.openmodinstaller.ui.createUI

fun main() = Window(resizable = false, size = IntSize(850, 650)) {
    // Init discovery
    ModJSONDiscovery.load(Properties.cacheFolderField)

    createUI()
}