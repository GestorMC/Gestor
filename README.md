# OpenModInstaller
`OpenModInstaller` is an innovative open-source application for universal Minecraft mod management.

# About

It combines three types of tools into a single app:

1. Mod and modpack hosting (CurseForge, Modrinth) with Markdown pages (CommonMark3 standard)
2. Automatic mod installing/updating/removing
3. Launching Minecraft (Forge, Fabric and Quilt) with multiple profiles

The app is powered by the [Kotlin language](https://kotlinlang.org/) and [JetPack Compose Desktop](https://www.jetbrains.com/lp/compose/).

# Development

This product is currently in a early alpha development stage.

Alpha releases are expected to be available in the middle of July 2021.

# Features

Here's an exhaustive list of features coming into the 1.0 release
and the status of their development:

- [x] Settings
- [ ] JSON
  - [x] Universal JSON format for developers
  - [x] Manual URL discovery
  - [x] Search from discovered
  - [ ] Autocomplete discovery
- [ ] Mod management
  - [ ] Mod downloading (coming soon)
  - [ ] Mod updating (coming soon)
  - [ ] Mod removing (coming soon)
- [ ] Mod hosting
  - [ ] Markdown pages for mods (coming soon)
  - [ ] Metadata display for mods
  - [ ] Release display for mods
  - [ ] Integration of mod downloading, updating and removing
- [ ] Mod suggestions
  - [ ] Calculating downloads for mods to display popularity
  - [ ] Recommending mods
  - [ ] Starring mods
  - [ ] Following mods
  - [ ] Advanced search
- [ ] Modpacks
  - [ ] Creating modpacks
  - [ ] Viewing modpacks
  - [ ] Installing modpacks
- [ ] Launching Minecraft
  - [ ] Launching via the [GJLMC](https://github.com/GoodTimeStudio/GoodTime-Java-Minecraft-Launcher-Core) core
  - [ ] Multiple profiles
  - [ ] Multiple modloaders (Forge, Fabric and Quilt)
  - [ ] Launching modpacks

# Download

Since the app is in early development, no downloads are available.

You can compile the app to a native binary following these simple steps:

1. Clone the repository on your device
2. Make sure that you have [AdoptOpenJDK 11 HotSpot](https://adoptopenjdk.net/) installed and added to the `JAVA_PATH` variable
3. Open the command-line
4. Run `gradlew package` and wait for a bit
5. Go to the `$CLONED_FOLDER_PATH/build/compose/binaries` and the correct subfolder for
your binary (`msi` folder for Windows, for instance) and run the installer

# Team

The team consists of only me, the main developer.

Some anonymous people have been helping me indirectly with this project, but I would not like to reveal their names.

# Contribute

Any contributions, help and support would be very welcome!

Email me at `karpovanton729@gmail.com` for support. I may be slow to respond though.
