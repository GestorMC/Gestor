package com.redgrapefruit.openmodinstaller.consts

import com.redgrapefruit.openmodinstaller.data.LoaderType
import com.redgrapefruit.openmodinstaller.data.ModpackData
import com.redgrapefruit.openmodinstaller.data.ModpackState

val exampleModPack = ModpackData(
    0,
    "0.1.0",
    ModpackState.Installed,
    null,
    Triple(LoaderType.Vanilla, "1.17.1", null),
    "Example Modpack",
    "lucsoft",
    System.currentTimeMillis(),
    "",
    arrayOf()
)


val exampleModPackTwo = ModpackData(
    0,
    "0.1.0",
    ModpackState.Installed,
    null,
    Triple(LoaderType.Vanilla, "1.16.4", null),
    "Example Modpack 2",
    "lucsoft",
    System.currentTimeMillis(),
    "",
    arrayOf()
)


val exampleModPackOld = ModpackData(
    0,
    "0.1.0",
    ModpackState.Available,
    null,
    Triple(LoaderType.Vanilla, "1.15.0", null),
    "Example Modpack 2",
    "lucsoft",
    System.currentTimeMillis(),
    "",
    arrayOf()
)