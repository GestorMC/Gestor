package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.redgrapefruit.openmodinstaller.ui.components.Modpack

@Composable
fun Dashboard() {
    Row(modifier = Modifier.padding(23.dp).fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Row {
                Text("Discover", fontSize = 1.7.em, fontWeight = FontWeight.Black)
            }
        }
        Spacer(Modifier.width(13.dp))
        Column(Modifier.requiredWidth(240.dp)) {
            Row {
                Text("My Modpacks", fontSize = 1.7.em, fontWeight = FontWeight.Black)
            }
            
        }
    }
}