package com.gestormc.gestor.md

import com.gestormc.gestor.core.ModDiscovery
import com.gestormc.gestor.core.ModInstaller
import com.gestormc.gestor.util.Settings
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
