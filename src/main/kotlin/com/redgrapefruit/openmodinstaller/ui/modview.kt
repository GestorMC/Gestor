package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.redgrapefruit.openmodinstaller.data.mod.Mod

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
}

fun enableModviewWith(mod: Mod) {
    doModview = true
    modviewMod = mod
}
