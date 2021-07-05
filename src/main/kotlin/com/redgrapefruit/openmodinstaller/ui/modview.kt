package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.redgrapefruit.openmodinstaller.data.mod.Mod
import com.redgrapefruit.openmodinstaller.markdown.MDDocument
import com.redgrapefruit.openmodinstaller.markdown.downloadMarkdown
import com.redgrapefruit.openmodinstaller.util.Settings
import org.commonmark.node.Document
import org.commonmark.parser.Parser
import java.io.FileInputStream

var doModview: Boolean = false

private var modviewMod: Mod? = null

@Composable
fun createModview(enabled: Boolean) {
    if (!enabled) return

    if (!doModview && modviewMod == null) {
        Text(
            text = "No mods in the view",
            fontSize = 1.8.em,
            modifier = Modifier.padding(200.dp, 170.dp)
        )
        return
    }

    // Download MD and read it. Also delete if needed
    val markdownFile = downloadMarkdown(modviewMod!!.meta.page)
    val markdownText: String
    FileInputStream(markdownFile).use { stream ->
        markdownText = stream.readBytes().decodeToString()
    }
    if (!Settings.storeCaches) {
        markdownFile.delete()
    }

    // Render MD
    val parser = Parser.builder().build()
    val root = parser.parse(markdownText) as Document

    MDDocument(root)
}

fun enableModviewWith(mod: Mod) {
    doModview = true
    modviewMod = mod
}
