@file:Suppress("FunctionName")

package com.redgrapefruit.openmodinstaller.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
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
import com.redgrapefruit.openmodinstaller.ui.helper.getBackgroundImageFromVersion

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
        if (value) {
            Image(imageResource("drawable/overlay.png"), "", Modifier
                    .aspectRatio(2f)
                    .fillMaxWidth())
            Button(onClick = {

            }, Modifier.align(Alignment.BottomStart).padding(11.dp)) {
                Text("Install")
            }
            Text(data.displayName, Modifier.padding(11.dp), color = Color.White, fontWeight = FontWeight.Bold, fontSize = .88.em)
        }

    }
}