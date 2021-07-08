package com.redgrapefruit.openmodinstaller.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

// UI for the homepage

@Composable
fun renderHomepage() {
    discoverButton(currentColors)
    Column(modifier = Modifier.padding(77.dp, 244.dp)) { Row { modImage() } }
}

/**
 *
 */
@Composable
fun discoverButton(colors: Colors) {
    Column(modifier = Modifier.padding(77.dp, 137.dp)) {
        Row {
            Text(
                text = "Discover",
                fontSize = 2.1.em,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface,
                modifier = Modifier
                    .width(196.dp)
                    .height(59.dp)
            )
        }
    }
}

@Composable
fun modImage() {
    Image(
        bitmap = imageResource("unknown_img.png"),
        contentDescription = "indeed",
        modifier = Modifier
            .width(350.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(20))
    )
}
