package com.redgrapefruit.openmodinstaller.ui.md

import com.redgrapefruit.openmodinstaller.core.ModDiscovery
import com.redgrapefruit.openmodinstaller.core.ModInstaller
import com.redgrapefruit.openmodinstaller.util.Settings
import java.io.File
import kotlin.random.Random

/**
 * Downloads a MD file from [url]
 */
fun downloadMarkdown(url: String): File {
    ModDiscovery.initLocal()
    val installerFolder = File(Settings.cacheFolderField).parent
    val markdownFolder = File("$installerFolder/markdown")

    if (!markdownFolder.exists()) {
        markdownFolder.mkdirs()
    }

    val markdownFile = File("$markdownFolder/download_${Random.nextInt(Int.MAX_VALUE)}")
    markdownFile.createNewFile()

    ModInstaller.downloadFile(url, markdownFile.absolutePath)

    return markdownFile
}
