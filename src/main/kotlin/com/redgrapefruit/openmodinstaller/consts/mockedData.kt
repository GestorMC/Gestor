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

val exampleModPackWithCustomBackground = ModpackData(
    packageVersion = 0,
    "1.0.0",
    ModpackState.Available,
    Pair("https://raw.githubusercontent.com/OpenModInstaller/OpenModInstaller/745e6de46602fc1033976470e312ae65dcd732a0/src/main/resources/drawable/ExampleModpack.png", null),
    Triple(LoaderType.Fabric, "1.17.1", "0.11.6"),
    "EXAMPLE MODPACK",
    "lucsoft",
    System.currentTimeMillis(),
    "Example Modpack description",
    arrayOf()
)

val exampleModPackWithCustomBackgroundFull = ModpackData(
    packageVersion = 0,
    "1.0.1",
    ModpackState.Installed,
    Pair("https://raw.githubusercontent.com/OpenModInstaller/OpenModInstaller/745e6de46602fc1033976470e312ae65dcd732a0/src/main/resources/drawable/ExampleModpack.png",
        "https://raw.githubusercontent.com/OpenModInstaller/OpenModInstaller/ada2d70218378592d04053b60349daee6cd415f6/src/main/resources/drawable/layerTwo/ExampleModpack.png"),
    Triple(LoaderType.Fabric, "1.17.1", "0.11.6"),
    "EXAMPLE MODPACK 2",
    "lucsoft",
    System.currentTimeMillis(),
    "Example Modpack description",
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