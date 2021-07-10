package com.redgrapefruit.openmodinstaller.ui.helper

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource

@Composable
fun getBackgroundImageFromVersion(version: String): ImageBitmap {
    if (version.startsWith("1.17"))
        return imageResource("drawable/mc_1.17.png")
    else if (version.startsWith("1.16"))
        return imageResource("drawable/mc_1.16.png")
    else
        return imageResource("drawable/mc.png")
}
