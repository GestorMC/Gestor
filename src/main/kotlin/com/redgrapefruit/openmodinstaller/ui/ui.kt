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
    var settingsEnabled by remember { mutableStateOf(false) }
    var discoveryEnabled by remember { mutableStateOf(false) }

    MaterialTheme {
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
                        settingsEnabled = false
                        discoveryEnabled = false
                    },
                    selected = selectedTab == 0
                )
                Tab(
                    content = {
                        Text(
                            text = "Settings",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    onClick =  {
                        selectedTab = 1
                        homeEnabled = false
                        settingsEnabled = true
                        discoveryEnabled = false
                    },
                    selected = selectedTab == 1
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
                        selectedTab = 2
                        homeEnabled = false
                        settingsEnabled = false
                        discoveryEnabled = true
                    },
                    selected = selectedTab == 2
                )
            },
            modifier = Modifier.height(35.dp)
        )

        createHome(homeEnabled)
        createSettings(settingsEnabled)
        createDiscovery(discoveryEnabled)
    }
}

@Composable
fun createHome(enabled: Boolean) {
    if (!enabled) return


}

@Composable
fun createSettings(enabled: Boolean) {
    if (!enabled) return

    
}

@Composable
fun createDiscovery(enabled: Boolean) {
    if (!enabled) return


}
