@file:Suppress("FunctionName")

package com.redgrapefruit.openmodinstaller.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.redgrapefruit.openmodinstaller.data.*
import com.redgrapefruit.openmodinstaller.ui.helper.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Modpack(data: ModpackData) {
    val (value, setValue) = remember { mutableStateOf(false) }

    Box(Modifier
        .pointerMoveFilter(
            onEnter = { setValue(true); false },
            onExit = {setValue(false); false}
        )
        .clip(RoundedCornerShape(10.dp))
        .fillMaxWidth(),
    ) {
        Image(getImageFromVersion(data.loader.second), "", Modifier.aspectRatio(2f).fillMaxWidth())

        // This is currently a somewhat hacky way to add the inside shadows
        // TODO CHANGE THIS TO A RUNTIME SHADOW
        AnimatedVisibility(value && data.state != ModpackState.Installing, enter = fadeIn(), exit = fadeOut()) {
            if(data.image != null && data.image.second == null)
                Image(imageResource("drawable/overlay_top.png"), "", Modifier.aspectRatio(2f).fillMaxWidth())
            Image(imageResource("drawable/overlay_bottom.png"), "", Modifier.aspectRatio(2f).fillMaxWidth())
        }

        LayerTwoRender(data, data.image, value)

        Column(Modifier.aspectRatio(2f).fillMaxSize(),verticalArrangement = Arrangement.SpaceBetween) {
            val visible = value && data.image != null && data.image.second == null;
            AnimatedVisibility(visible) {
                Text(data.displayName, Modifier.padding(10.dp), color = MaterialTheme.colors.primary, fontWeight = FontWeight.Medium, fontSize = .9.em)
            }
            AnimatedVisibility(visible) {
                ButtonBar(data, value)
            }
        }

        if(data.state == ModpackState.Installing)
            CircularProgressIndicator(Modifier.align(Alignment.Center).width(35.dp))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun LayerTwoRender(
    data: ModpackData,
    image: Pair<String, LayerTwoLocation>?,
    value: Boolean
) {
    if (data.image == null || image?.second != null)
        Column(
            Modifier.aspectRatio(240f / 120f).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            if (data.state != ModpackState.Installing)
                AnimatedVisibility(value) { Spacer(Modifier.fillMaxWidth().aspectRatio(240f / 10f)) }
            Image(getLayerTwoImageFromVersion(data.loader.second), "", Modifier.aspectRatio(240f / 54f))
            ButtonBar(data, value)
        }
}