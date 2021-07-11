@file:Suppress("FunctionName")

package com.redgrapefruit.openmodinstaller.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.redgrapefruit.openmodinstaller.data.ModpackData
import com.redgrapefruit.openmodinstaller.data.ModpackState
import com.redgrapefruit.openmodinstaller.ui.helper.getBackgroundImageFromVersion
import org.jetbrains.skija.FilterBlurMode

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
            getBackgroundImageFromVersion(data.loader.second.split('+')[0]),
            "",
            Modifier
                .aspectRatio(2f)
                .fillMaxWidth()

        )
        if(data.state == ModpackState.Installing)
            CircularProgressIndicator(Modifier.align(Alignment.Center).width(35.dp))

        if (value) {
            // This is currently a somewhat hacky way to add the inside shadows
            // TODO CHANGE THIS TO A RUNTIME SHADOW
            Image(imageResource(if (data.state == ModpackState.Installing) "drawable/overlay_onlytop.png" else "drawable/overlay.png"), "", Modifier
                    .aspectRatio(2f)
                    .fillMaxWidth())

            Row(Modifier.align(Alignment.BottomStart).padding(10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (data.state == ModpackState.Available) {
                    Button(onClick = { }, elevation = null) { Text("GET") }
                } else if (data.state == ModpackState.Installed) {
                    Button(onClick = { }, elevation = null) { Text("PLAY NOW") }
                    Button(onClick = { }, elevation = null, colors = ButtonDefaults.textButtonColors()) {
                        Text("EDIT")
                    }
                }
            }

            Text(data.displayName, Modifier.padding(10.dp), color = MaterialTheme.colors.primary, fontWeight = FontWeight.Medium, fontSize = .9.em)
        }

    }
}