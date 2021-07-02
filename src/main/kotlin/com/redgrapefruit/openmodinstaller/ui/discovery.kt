package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.svgResource
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
                    ModJSONDiscovery.discover(urlFieldText, Properties.cacheFolderField, true)
                },
                content = {
                    Text("Submit")
                },
                modifier = Modifier.padding(15.dp, 10.dp)
            )
        }
        Row(modifier = Modifier.padding(0.dp, 20.dp)) {
            Text(
                text = "Available sources: ",
                fontSize = 1.4.em,
                fontWeight = FontWeight.SemiBold
            )
        }
        ModJSONDiscovery.database.forEach { source ->
            var expanded by remember { mutableStateOf(false) }

            Row(modifier = Modifier.padding(0.dp, 10.dp)) {
                Text(
                    text = source.meta.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 1.2.em
                )
                Button(
                    onClick = {
                        expanded = !expanded
                    },
                    content = {
                        Image(
                            painter = svgResource("arrow.svg"),
                            contentDescription = null,
                            modifier = Modifier.width(20.dp).height(20.dp)
                        )
                    },
                    colors = ButtonDefaults.textButtonColors(backgroundColor = Color.White),
                    modifier = Modifier.padding(10.dp, 0.dp)
                )
                Button(
                    onClick = {
                        ModJSONDiscovery.database.remove(source)
                    },
                    content = {
                        Text("Remove")
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red, contentColor = Color.White),
                    modifier = Modifier.padding(15.dp, 0.dp)
                )
            }

            if (expanded) {
                Row {
                    Text(
                        text = "Name: ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(source.meta.name)
                }
                Row {
                    Text(
                        text = "Host: ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(source.meta.host)
                }
                Row {
                    Text(
                        text = "Provider: ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(source.meta.provider.name)
                }
            }
        }
    }
}
