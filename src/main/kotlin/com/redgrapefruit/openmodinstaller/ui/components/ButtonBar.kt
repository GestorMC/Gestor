package com.redgrapefruit.openmodinstaller.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.redgrapefruit.openmodinstaller.data.ModpackData
import com.redgrapefruit.openmodinstaller.data.ModpackState


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ColumnScope.ButtonBar(
    data: ModpackData,
    value: Boolean
) {
    if (data.state != ModpackState.Installing)
        AnimatedVisibility(value) {
            Column(Modifier.fillMaxWidth().aspectRatio(240f / 56f), verticalArrangement = Arrangement.SpaceBetween) {
                Spacer(Modifier.size(1.dp))
                Row(
                    Modifier.padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (data.state == ModpackState.Available) {
                        Button(onClick = { }, elevation = null) { Text("GET") }
                    } else if (data.state == ModpackState.Installed) {
                        Button(onClick = { }, elevation = null) { Text("PLAY NOW") }
                        Button(onClick = { }, elevation = null, colors = ButtonDefaults.textButtonColors()) {
                            Text("EDIT")
                        }
                    }
                }
            }
        }
}
