package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.redgrapefruit.openmodinstaller.core.ModJSONDiscovery

@Composable
fun createSearch(enabled: Boolean) {
    if (!enabled) return

    var searchField by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(40.dp, 75.dp)) {
        Row {
            // Search
            Text(
                text = "Search: ",
                fontSize = 1.7.em,
                fontWeight = FontWeight.SemiBold
            )
            TextField(
                value = searchField,
                onValueChange = { new -> searchField = new },
                textStyle = TextStyle(fontSize = 1.4.em),
                modifier = Modifier.scale(0.8f)
            )
        }
    }

    // Display results if search field isn't empty
    if (searchField.isBlank()) return

    val results = ModJSONDiscovery.searchMods(searchField)

    if (results.isEmpty()) {
        Row(modifier = Modifier.padding(300.dp, 200.dp)) {
            Text(
                text = "No results",
                fontSize = 1.6.em
            )
        }
    }

    results.forEach { mod ->
        Row(modifier = Modifier.padding(40.dp, 170.dp)) {
            Text(mod.meta.name)
        }
    }
}