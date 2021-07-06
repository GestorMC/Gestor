@file:Suppress("FunctionName")

package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.layout.*
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
import com.redgrapefruit.openmodinstaller.util.Settings

@Composable
fun createSettings(enabled: Boolean) {
    if (!enabled) return

    Column(modifier = Modifier.padding(40.dp, 75.dp)) {
        ModsFolderField()
        CacheFolderField()
        ReleaseTypeDropdown()

        Spacer(modifier = Modifier.height(30.dp))

        UseUnverifiedSourcesCheckbox()
        StoreCachesCheckbox()
        UseAutocompleteCheckbox()
    }
}

@Composable
private fun ModsFolderField() {
    var modsFolderField by remember { mutableStateOf(Settings.modsFolderField) }

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
            onValueChange = { new -> modsFolderField = new; Settings.modsFolderField = new },
            textStyle = TextStyle(fontSize = 1.2.em)
        )
    }
}

@Composable
private fun CacheFolderField() {
    var cacheFolderField by remember { mutableStateOf(Settings.cacheFolderField) }

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
            onValueChange = { new -> cacheFolderField = new; Settings.cacheFolderField = new },
            textStyle = TextStyle(fontSize = 1.2.em)
        )
    }
}

@Composable
private fun ReleaseTypeDropdown() {
    var releaseTypeDropdownExpanded by remember { mutableStateOf(false) }
    var releaseTypeDropdownBoldItem by remember { mutableStateOf(1) }

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
                ReleaseTypeDropdownItem(
                    ReleaseType.LTS,
                    "LTS (Long Term Support)",
                    { releaseTypeDropdownBoldItem = 0 },
                    { if (releaseTypeDropdownBoldItem == 0) FontWeight.Bold else FontWeight.Normal })

                ReleaseTypeDropdownItem(
                    ReleaseType.Stable,
                    "Stable",
                    { releaseTypeDropdownBoldItem = 1 },
                    { if (releaseTypeDropdownBoldItem == 1) FontWeight.Bold else FontWeight.Normal })

                ReleaseTypeDropdownItem(
                    ReleaseType.RC,
                    "RC (Release Candidate)",
                    { releaseTypeDropdownBoldItem = 2 },
                    { if (releaseTypeDropdownBoldItem == 2) FontWeight.Bold else FontWeight.Normal })

                ReleaseTypeDropdownItem(
                    ReleaseType.Preview,
                    "Preview",
                    { releaseTypeDropdownBoldItem = 3 },
                    { if (releaseTypeDropdownBoldItem == 3) FontWeight.Bold else FontWeight.Normal })

                ReleaseTypeDropdownItem(
                    ReleaseType.EAP,
                    "EAP (Early Access Program)",
                    { releaseTypeDropdownBoldItem = 4 },
                    { if (releaseTypeDropdownBoldItem == 4) FontWeight.Bold else FontWeight.Normal })

                ReleaseTypeDropdownItem(
                    ReleaseType.Beta,
                    "Beta",
                    { releaseTypeDropdownBoldItem = 5 },
                    { if (releaseTypeDropdownBoldItem == 5) FontWeight.Bold else FontWeight.Normal })

                ReleaseTypeDropdownItem(
                    ReleaseType.Alpha,
                    "Alpha",
                    { releaseTypeDropdownBoldItem = 6 },
                    { if (releaseTypeDropdownBoldItem == 6) FontWeight.Bold else FontWeight.Normal })

                ReleaseTypeDropdownItem(
                    ReleaseType.BetaPreview,
                    "Beta Preview",
                    { releaseTypeDropdownBoldItem = 7 },
                    { if (releaseTypeDropdownBoldItem == 7) FontWeight.Bold else FontWeight.Normal })

                ReleaseTypeDropdownItem(
                    ReleaseType.AlphaPreview,
                    "Alpha Preview",
                    { releaseTypeDropdownBoldItem = 8 },
                    { if (releaseTypeDropdownBoldItem == 8) FontWeight.Bold else FontWeight.Normal })

                ReleaseTypeDropdownItem(
                    ReleaseType.Snapshot,
                    "Snapshot",
                    { releaseTypeDropdownBoldItem = 9 },
                    { if (releaseTypeDropdownBoldItem == 9) FontWeight.Bold else FontWeight.Normal })

                ReleaseTypeDropdownItem(
                    ReleaseType.Nightly,
                    "Nightly",
                    { releaseTypeDropdownBoldItem = 10 },
                    { if (releaseTypeDropdownBoldItem == 10) FontWeight.Bold else FontWeight.Normal })
            }
        )
    }
}

@Composable
private inline fun ReleaseTypeDropdownItem(
    releaseType: ReleaseType,
    text: String,
    crossinline indexSetter: () -> Unit,
    crossinline fontWeightSelector: () -> FontWeight
) {
    DropdownMenuItem(
        onClick = {
            Settings.chosenReleaseType = releaseType
            indexSetter.invoke()
        },
        content = {
            Text(
                text = text,
                fontWeight = fontWeightSelector.invoke()
            )
        }
    )
}

@Composable
private fun UseUnverifiedSourcesCheckbox() {
    var useUnverifiedSources by remember { mutableStateOf(Settings.useUnverifiedSources) }

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
            onCheckedChange = { new -> useUnverifiedSources = new; Settings.useUnverifiedSources = new },
            modifier = Modifier.padding(10.dp, 20.dp)
        )
    }
}

@Composable
private fun StoreCachesCheckbox() {
    var storeCaches by remember { mutableStateOf(Settings.storeCaches) }

    Row {
        // Store caches after download
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
            onCheckedChange = { new -> storeCaches = new; Settings.storeCaches = new },
            modifier = Modifier.padding(10.dp, 20.dp)
        )
    }
}

@Composable
private fun UseAutocompleteCheckbox() {
    var useAutocomplete by remember { mutableStateOf(Settings.useAutocomplete) }

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
            onCheckedChange = { new -> useAutocomplete = new; Settings.useAutocomplete = new },
            modifier = Modifier.padding(10.dp, 20.dp)
        )
    }
}
