@file:Suppress("FunctionName")

package com.redgrapefruit.openmodinstaller.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
        Image(
            getImageFromVersion(data.loader.second),
            "",
            Modifier
                .aspectRatio(2f)
                .fillMaxWidth()

        )

        // This is currently a somewhat hacky way to add the inside shadows
        // TODO CHANGE THIS TO A RUNTIME SHADOW
        AnimatedVisibility(value && data.state != ModpackState.Installing, enter = fadeIn(), exit = fadeOut()) {
            if(data.image != null && data.image.second == null)
                Image(imageResource("drawable/overlay_top.png"), "", Modifier.aspectRatio(2f).fillMaxWidth())
            Image(imageResource("drawable/overlay_bottom.png"), "", Modifier.aspectRatio(2f).fillMaxWidth())
        }

        Column(Modifier.aspectRatio(240f / 120f).fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End) {
            if(data.state != ModpackState.Installing)
                AnimatedVisibility(value) {
                    Spacer(Modifier.fillMaxWidth().aspectRatio(240f / 10f))
                }
            Image(getLayerTwoImageFromVersion(data.loader.second),"", Modifier.aspectRatio(240f / 54f))
            if(data.state != ModpackState.Installing)
                AnimatedVisibility(value) {
                    Column(Modifier.fillMaxWidth().aspectRatio(240f / 56f), verticalArrangement = Arrangement.SpaceBetween) {
                        Spacer(Modifier.size(1.dp))
                        Row(Modifier.padding(10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
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

        if(value && data.image != null && data.image.second == null)
                Text(data.displayName, Modifier.padding(10.dp), color = MaterialTheme.colors.primary, fontWeight = FontWeight.Medium, fontSize = .9.em)

        if(data.state == ModpackState.Installing)
            CircularProgressIndicator(Modifier.align(Alignment.Center).width(35.dp))
    }
}