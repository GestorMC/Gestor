@file:Suppress("FunctionName")

package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.redgrapefruit.openmodinstaller.data.mod.Mod
import com.redgrapefruit.openmodinstaller.markdown.MDDocument
import com.redgrapefruit.openmodinstaller.markdown.downloadMarkdown
import com.redgrapefruit.openmodinstaller.util.Settings
import org.commonmark.node.Document
import org.commonmark.parser.Parser
import java.io.FileInputStream

/**
 * Is the modview currently enabled
 */
var doModview: Boolean = false

/**
 * The target of the modview
 */
private var modviewMod: Mod? = null

@Composable
fun createModview(enabled: Boolean) {
    if (!enabled) return

    if (!doModview && modviewMod == null) {
        NoModsInTheView()
        return
    }

    Column {
        ActionButtons()
    }
    ModMarkdown()
}

fun enableModviewWith(mod: Mod) {
    doModview = true
    modviewMod = mod
}

@Composable
private fun ActionButtons() {
    Row(modifier = Modifier.padding(20.dp, 50.dp)) {
        ModButton(true, "Download", Color.Green) {
            // TODO: Download code
        }
        ModButton(false, "Update", Color.Blue) {
            // TODO: Update code
        }
        ModButton(false, "Check for updates", Color(173, 216, 230)) {
            // TODO: Update checking code
        }
        ModButton(false, "Remove", Color.Red) {
            // TODO: Removal code
        }
    }
}

@Composable
private fun ModButton(first: Boolean, text: String, color: Color, clickAction: () -> Unit) {
    Button(
        onClick = clickAction,
        colors = ButtonDefaults.buttonColors(backgroundColor = color, contentColor = Color.White),
        content = {
            Text(
                text = text,
                fontSize = 1.1.em,
                fontWeight = FontWeight.SemiBold
            )
        },
        modifier = if (first) Modifier else Modifier.padding(20.dp, 0.dp)
    )
}

@Composable
private fun NoModsInTheView() {
    Text(
        text = "No mods in the view",
        fontSize = 1.8.em,
        modifier = Modifier.padding(200.dp, 170.dp)
    )
}

@Composable
private fun ModMarkdown() {
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
