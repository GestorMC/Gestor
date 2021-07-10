package com.redgrapefruit.openmodinstaller.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.redgrapefruit.openmodinstaller.data.ModpackData

@Composable
fun GridModpackList(discoverArray: Array<ModpackData>, myModpacksArray: Array<ModpackData>) {
    val (value, setValue) = remember { mutableStateOf(IntSize.Zero) }

    Column (Modifier.onSizeChanged { coordinates -> setValue(coordinates) }) {
        if (value.width <= 1550) SmallGridModpackList(discoverArray, myModpacksArray)
        else BigGridModpackList(discoverArray, myModpacksArray)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmallGridModpackList(discoverArray: Array<ModpackData>, myModpacksArray: Array<ModpackData>) {
    Column {
        Column(Modifier.padding(top = 21.dp)) {
            ModPackListTitle("My Modpacks")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(13.dp),
                contentPadding = PaddingValues(bottom = 21.dp, start = 21.dp, end = 21.dp),
                modifier = Modifier.height((120 + 21).dp)
            ) {
                items(myModpacksArray) { data ->
                    Modpack(data)
                }
            }
        }
        Column {
            ModPackListTitle("Discover")
            LazyVerticalGrid(
                cells = GridCells.Adaptive((240 + 21 + 5).dp),
                contentPadding = PaddingValues(start = 21.dp, bottom = 8.dp, end = 8.dp)
            ) {
                items(discoverArray) { data ->
                    Row(Modifier.padding(end = 13.dp, bottom = 13.dp)) {
                        Modpack(data)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BigGridModpackList(discoverArray: Array<ModpackData>, myModpacksArray: Array<ModpackData>) {
    Row(
        modifier = Modifier
            .padding(horizontal = 21.dp)
            .padding(top = 21.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(2.108f)) {
            ModPackListTitleNoPadding("Discover")
            LazyVerticalGrid(
                cells = GridCells.Adaptive(240.dp),
                contentPadding = PaddingValues(bottom = 13.dp)
            ) {
                items(discoverArray) { data ->
                    Row(Modifier.padding(end = 13.dp, bottom = 13.dp)) {
                        Modpack(data)
                    }
                }
            }
        }
        Column(Modifier.weight(1f)) {
            ModPackListTitleNoPadding("My Modpacks")
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(13.dp),
                contentPadding = PaddingValues(bottom = 13.dp)
            ) {
                items(myModpacksArray) { data ->
                    Modpack(data)
                }
            }
        }
    }
}