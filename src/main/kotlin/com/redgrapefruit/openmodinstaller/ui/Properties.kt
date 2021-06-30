package com.redgrapefruit.openmodinstaller.ui

import com.sun.security.auth.module.NTSystem

object Properties {
    private val nt = NTSystem()

    var modsFolderField = "C:/Users/${nt.name}/AppData/Roaming/.minecraft/mods"
    var cacheFolderField = "C:/Users/${nt.name}/.openmodinstaller/cache"
    var useUnverifiedSources = true
}