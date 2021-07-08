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
    modImage(0, 0)
    modImage(1, 0)
    modImage(0, 1)
    modImage(1, 1)
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
fun modImage(x: Int, y: Int) {
    val padX = (77 + x * 388).dp
    val padY = (244 + y * 232).dp

    Image(
        bitmap = imageResource("unknown_img.png"),
        contentDescription = null,
        modifier = Modifier
            .padding(padX, padY)
            .width(350.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(20))
    )
}
