package com.redgrapefruit.openmodinstaller

import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import com.redgrapefruit.openmodinstaller.ui.createUI

fun main() = Window(resizable = false) {
    createUI()
}