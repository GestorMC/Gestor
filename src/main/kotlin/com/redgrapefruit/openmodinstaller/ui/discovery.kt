package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.redgrapefruit.openmodinstaller.core.ModJSONDiscovery

@Composable
fun createDiscovery(enabled: Boolean) {
    if (!enabled) return

    var urlFieldText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(40.dp, 75.dp)) {
        Row {
            // Discovery via URL
            // Label
            Text(
                text = "Discover via URL: ",
                fontSize = 1.5.em,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(0.dp, 10.dp)
            )
        }
        Row {
            // Field
            TextField(
                value = urlFieldText,
                onValueChange = { new -> urlFieldText = new }
            )
            // Submit button
            Button(
                onClick = {
                    ModJSONDiscovery.discover(urlFieldText, Properties.cacheFolderField)
                },
                content = {
                    Text("Submit")
                },
                modifier = Modifier.padding(15.dp, 10.dp)
            )
        }
    }
}
