package com.gestormc.gestor.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.gestormc.gestor.task.getBitmapFromURL
import com.gestormc.gestor.task.getCachedBitmap

@Composable
fun CachedImage(
    imageUrl: String,
    modifier: Modifier
) {
    val (loading, setLoading) = remember { mutableStateOf(false) }

    val bitmapImage = getCachedBitmap(imageUrl)
    if (!loading && bitmapImage == null) {
        LaunchedEffect(true) {
            getBitmapFromURL(imageUrl)
            setLoading(true);
        }
    }
    if (bitmapImage != null) Image(bitmapImage, "", modifier)
}