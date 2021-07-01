package com.redgrapefruit.openmodinstaller.ui

import com.sun.security.auth.module.NTSystem
import com.redgrapefruit.openmodinstaller.data.distribution.DistributionSource
import com.redgrapefruit.openmodinstaller.data.mod.ReleaseType

object Properties {
    private val nt = NTSystem()

    /**
     * The target folder for downloading mods
     */
    var modsFolderField = "C:/Users/${nt.name}/AppData/Roaming/.minecraft/mods"

    /**
     * The folder for storing cache
     */
    var cacheFolderField = "C:/Users/${nt.name}/.openmodinstaller/cache"

    /**
     * Use unverified [DistributionSource]s
     */
    var useUnverifiedSources = true

    /**
     * Store caches or not.
     *
     * If not, the caches will immediately be deleted after use.
     */
    var storeCaches = true

    /**
     * Use autocomplete for connecting to distribution sources
     */
    var useAutocomplete = true

    /**
     * The target [ReleaseType] for downloading
     */
    var chosenReleaseType = ReleaseType.Stable
}