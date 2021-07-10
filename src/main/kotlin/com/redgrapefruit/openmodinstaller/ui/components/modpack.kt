@file:Suppress("FunctionName")

package com.redgrapefruit.openmodinstaller.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.redgrapefruit.openmodinstaller.data.ModpackData
import com.redgrapefruit.openmodinstaller.ui.helper.getBackgroundImageFromVersion

@Composable
fun Modpack(data: ModpackData) {
    Box(Modifier
        .clip(RoundedCornerShape(10.dp))
        .fillMaxWidth()
    ) {
        Image(
            getBackgroundImageFromVersion(data.loader.second.split('+')[0]),
            "",
            Modifier
                .aspectRatio(2f)
                .fillMaxWidth()
        )
    }
}