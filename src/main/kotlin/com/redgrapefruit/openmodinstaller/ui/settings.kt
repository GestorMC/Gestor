package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.sun.security.auth.module.NTSystem

@Composable
fun createSettings(enabled: Boolean) {
    if (!enabled) return

    var modsFolderField by remember { mutableStateOf("C:/Users/${NTSystem().name}/AppData/Roaming/.minecraft/mods") }

    Column(modifier = Modifier.padding(40.dp, 75.dp)) {
        Row {
            // Mods folder
            // Label
            Text(
                text = "Mods folder: ",
                fontSize = 1.7.em,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(0.dp, 10.dp)
            )
            // Text Area
            TextField(
                value = modsFolderField,
                onValueChange = { new -> modsFolderField = new },
                modifier = Modifier,
                textStyle = TextStyle(fontSize = 1.3.em)
            )
        }
    }
}