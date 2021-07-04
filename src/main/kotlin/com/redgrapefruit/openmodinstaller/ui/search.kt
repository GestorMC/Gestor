package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.svgResource
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

    var padding = 0
    var num = 1

    ScrollbarAdapter(ScrollState(0))

    results.forEach { mod ->
        Row(modifier = Modifier.padding(40.dp, (170 + padding).dp)) {
            Text(
                text = "$num. ${mod.meta.name}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 1.4.em
            )

            Button(
                onClick = {
                    enableModviewWith(mod)
                },
                content = {
                    Text(
                        text = "Open",
                        color = Color.White
                    )
                },
                modifier = Modifier.padding(20.dp, 0.dp)
            )
        }

        padding += 60
        ++num
    }
}
