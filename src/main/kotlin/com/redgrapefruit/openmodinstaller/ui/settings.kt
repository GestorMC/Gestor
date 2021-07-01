package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.redgrapefruit.openmodinstaller.data.mod.ReleaseType
import com.sun.security.auth.module.NTSystem

private val ntSystem = NTSystem()

@Composable
fun createSettings(enabled: Boolean) {
    if (!enabled) return

    var modsFolderField by remember { mutableStateOf(Properties.modsFolderField) }
    var cacheFolderField by remember { mutableStateOf(Properties.cacheFolderField) }
    var useUnverifiedSources by remember { mutableStateOf(Properties.useUnverifiedSources) }
    var storeCaches by remember { mutableStateOf(Properties.storeCaches) }
    var useAutocomplete by remember { mutableStateOf(Properties.useAutocomplete) }
    var releaseTypeDropdownExpanded by remember { mutableStateOf(false) }
    var releaseTypeDropdownBoldItem by remember { mutableStateOf(1) }

    Column(modifier = Modifier.padding(40.dp, 75.dp)) {
        Row {
            // Mods folder
            // Label
            Text(
                text = "Mods folder: ",
                fontSize = 1.4.em,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(0.dp, 10.dp)
            )
            // Text Area
            TextField(
                value = modsFolderField,
                onValueChange = { new -> modsFolderField = new; Properties.modsFolderField = new },
                textStyle = TextStyle(fontSize = 1.2.em)
            )
        }
        Row(modifier = Modifier.padding(0.dp, 20.dp)) {
            // Cache folder
            // Label
            Text(
                text = "Cache folder: ",
                fontSize = 1.4.em,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(0.dp, 10.dp)
            )
            // Text Area
            TextField(
                value = cacheFolderField,
                onValueChange = { new -> cacheFolderField = new; Properties.cacheFolderField = new },
                textStyle = TextStyle(fontSize = 1.2.em)
            )
        }
        Row(modifier = Modifier.padding(0.dp, 10.dp)) {
            // Target ReleaseType
            // Label
            Text(
                text = "Release type: ",
                fontSize = 1.4.em,
                fontWeight = FontWeight.SemiBold
            )
            // Button
            Button(
                onClick = { releaseTypeDropdownExpanded = !releaseTypeDropdownExpanded },
                content = {
                    Text(
                        text = "Select",
                        fontSize = 1.4.em,
                    )
                },
                colors = ButtonDefaults.textButtonColors(backgroundColor = Color.Black, contentColor = Color.White),
                modifier = Modifier.scale(0.8f)
            )
            // Menu
            DropdownMenu(
                expanded = releaseTypeDropdownExpanded,
                onDismissRequest = { releaseTypeDropdownExpanded = false },
                content = {
                    DropdownMenuItem(
                        onClick = {
                            Properties.chosenReleaseType = ReleaseType.LTS
                            releaseTypeDropdownBoldItem = 0
                        },
                        content = {
                            Text(
                                text = "LTS (Long Term Support)",
                                fontWeight =
                                    if (releaseTypeDropdownBoldItem == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Properties.chosenReleaseType = ReleaseType.Stable
                            releaseTypeDropdownBoldItem = 1
                        },
                        content = {
                            Text(
                                text = "Stable",
                                fontWeight =
                                    if (releaseTypeDropdownBoldItem == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Properties.chosenReleaseType = ReleaseType.RC
                            releaseTypeDropdownBoldItem = 2
                        },
                        content = {
                            Text(
                                text = "RC (Release Candidate)",
                                fontWeight =
                                    if (releaseTypeDropdownBoldItem == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Properties.chosenReleaseType = ReleaseType.Preview
                            releaseTypeDropdownBoldItem = 3
                        },
                        content = {
                            Text(
                                text = "Preview",
                                fontWeight =
                                    if (releaseTypeDropdownBoldItem == 3) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Properties.chosenReleaseType = ReleaseType.EAP
                            releaseTypeDropdownBoldItem = 4
                        },
                        content = {
                            Text(
                                text = "EAP (Early Access Program)",
                                fontWeight =
                                    if (releaseTypeDropdownBoldItem == 4) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Properties.chosenReleaseType = ReleaseType.Beta
                            releaseTypeDropdownBoldItem = 5
                        },
                        content = {
                            Text(
                                text = "Beta",
                                fontWeight =
                                    if (releaseTypeDropdownBoldItem == 5) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Properties.chosenReleaseType = ReleaseType.Alpha
                            releaseTypeDropdownBoldItem = 6
                        },
                        content = {
                            Text(
                                text = "Alpha",
                                fontWeight =
                                    if (releaseTypeDropdownBoldItem == 6) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Properties.chosenReleaseType = ReleaseType.BetaPreview
                            releaseTypeDropdownBoldItem = 7
                        },
                        content = {
                            Text(
                                text = "Beta Preview",
                                fontWeight =
                                    if (releaseTypeDropdownBoldItem == 7) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Properties.chosenReleaseType = ReleaseType.AlphaPreview
                            releaseTypeDropdownBoldItem = 8
                        },
                        content = {
                            Text(
                                text = "Alpha Preview",
                                fontWeight =
                                    if (releaseTypeDropdownBoldItem == 8) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Properties.chosenReleaseType = ReleaseType.Snapshot
                            releaseTypeDropdownBoldItem = 9
                        },
                        content = {
                            Text(
                                text = "Snapshot",
                                fontWeight =
                                    if (releaseTypeDropdownBoldItem == 9) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            Properties.chosenReleaseType = ReleaseType.Nightly
                            releaseTypeDropdownBoldItem = 10
                        },
                        content = {
                            Text(
                                text = "Nightly",
                                fontWeight =
                                    if (releaseTypeDropdownBoldItem == 10) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            )
        }
        Row(modifier = Modifier.padding(0.dp, 10.dp)) {
            // Use unverified distributions
            // Label
            Text(
                text = "Use unverified distributions: ",
                fontSize = 1.4.em,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(0.dp, 10.dp)
            )
            // Button
            Checkbox(
                checked = useUnverifiedSources,
                onCheckedChange = { new -> useUnverifiedSources = new; Properties.useUnverifiedSources = new },
                modifier = Modifier.padding(10.dp, 20.dp)
            )
        }
        Row {
            // Use NIO API to shorten download times
            // Label
            Text(
                text = "Store cache: ",
                fontSize = 1.4.em,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(0.dp, 10.dp)
            )
            // Button
            Checkbox(
                checked = storeCaches,
                onCheckedChange = { new -> storeCaches = new; Properties.storeCaches = new },
                modifier = Modifier.padding(10.dp, 20.dp)
            )
        }
        Row {
            // Use autocomplete
            // Label
            Text(
                text = "Use autocompletion: ",
                fontSize = 1.4.em,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(0.dp, 10.dp)
            )
            // Button
            Checkbox(
                checked = useAutocomplete,
                onCheckedChange = { new -> useAutocomplete = new; Properties.useAutocomplete = new },
                modifier = Modifier.padding(10.dp, 20.dp)
            )
        }
    }
}