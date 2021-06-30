package com.redgrapefruit.openmodinstaller

import androidx.compose.desktop.Window
import com.redgrapefruit.openmodinstaller.ui.createUI

fun main() = Window(resizable = false) {
    createUI()
}