package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun createUI() {
    var selectedTab by remember { mutableStateOf(0) }
    var homeEnabled by remember { mutableStateOf(true) }
    var searchEnabled by remember { mutableStateOf(false) }
    var settingsEnabled by remember { mutableStateOf(false) }
    var discoveryEnabled by remember { mutableStateOf(false) }
    var modviewEnabled by remember { mutableStateOf(doModview) }

    MaterialTheme(colors = lightColors()) {
        TabRow(
            selectedTabIndex = selectedTab,
            tabs = {
                Tab(
                    content = {
                        Text(
                            text = "Home",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    onClick = {
                        selectedTab = 0
                        homeEnabled = true
                        searchEnabled = false
                        settingsEnabled = false
                        discoveryEnabled = false
                        modviewEnabled = false
                    },
                    selected = selectedTab == 0
                )
                Tab(
                    content = {
                        Text(
                            text = "Search",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    onClick = {
                        selectedTab = 1
                        homeEnabled = false
                        searchEnabled = true
                        settingsEnabled = false
                        discoveryEnabled = false
                        modviewEnabled = false
                    },
                    selected = selectedTab == 1
                )
                Tab(
                    content = {
                        Text(
                            text = "Settings",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    onClick = {
                        selectedTab = 2
                        homeEnabled = false
                        searchEnabled = false
                        settingsEnabled = true
                        discoveryEnabled = false
                        modviewEnabled = false
                    },
                    selected = selectedTab == 2
                )
                Tab(
                    content = {
                        Text(
                            text = "Discovery",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    onClick = {
                        selectedTab = 3
                        homeEnabled = false
                        searchEnabled = false
                        settingsEnabled = false
                        discoveryEnabled = true
                        modviewEnabled = false
                    },
                    selected = selectedTab == 3
                )
                Tab(
                    content = {
                        Text(
                            text = "Modview",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    onClick = {
                        selectedTab = 4
                        homeEnabled = false
                        searchEnabled = false
                        settingsEnabled = false
                        discoveryEnabled = false
                        modviewEnabled = true
                    },
                    selected = selectedTab == 4
                )
            },
            modifier = Modifier.height(35.dp)
        )

        createHome(homeEnabled)
        createSearch(searchEnabled)
        createSettings(settingsEnabled)
        createDiscovery(discoveryEnabled)
        createModview(modviewEnabled)
    }
}

