@file:Suppress("FunctionName")

package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.redgrapefruit.openmodinstaller.core.ModDiscovery
import com.redgrapefruit.openmodinstaller.data.mod.Mod

@Composable
fun createSearch(enabled: Boolean) {
    if (!enabled) return

    val search = SearchField()
    val results = ModDiscovery.searchMods(search)

    SearchResults(results)
}

@Composable
private fun SearchField(): String {
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
    if (searchField.isBlank()) return searchField

    val results = ModDiscovery.searchMods(searchField)

    if (results.isEmpty()) {
        NoResultsInSearch()
    }

    return searchField
}

@Composable
private fun NoResultsInSearch() {
    Row(modifier = Modifier.padding(300.dp, 200.dp)) {
        Text(
            text = "No results",
            fontSize = 1.6.em
        )
    }
}

@Composable
private fun SearchResults(results: List<Mod>) {
    var searchResultIndex = 1

    val scrollState = rememberScrollState(0)

    Column(modifier = Modifier.padding(40.dp, 170.dp).verticalScroll(scrollState)) {
        results.forEach { mod ->
            Row(modifier = Modifier.padding(0.dp, 10.dp)) {
                Text(
                    text = "$searchResultIndex. ${mod.meta.name}",
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

            ++searchResultIndex
        }
    }
}
