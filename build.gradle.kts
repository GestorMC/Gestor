import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.20"
    id("org.jetbrains.compose") version "0.5.0-build245"
}

group = "com.redgrapefruit"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    maven { url = uri("https://libraries.minecraft.net/") } // libraries.minecraft.net has Authlib
}

dependencies {
    implementation(compose.desktop.currentOs) // JetPack Compose itself
    
    // --- Core dependencies ---
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2") // KotlinX Serialization. JSON support
    implementation("org.commonmark:commonmark:0.18.0") // CommonMark3. Markdown -> HTML rendering
    implementation("com.mojang:authlib:2.3.31")  // Mojang Authlib. Verification of accounts

    // --- Miscellaneous utilities ---
    implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "4.9.1") // OkHttp. The high-performance HTTP client used
    implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.12.0") // Commons Lang3. A misc utility library
    implementation(group = "org.apache.commons", name = "commons-compress", version = "1.20") // Commons Compress. Empowers all compression/decompression operations
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
}

compose.desktop {
    application {
        mainClass = "com.redgrapefruit.openmodinstaller.MainKt"
        nativeDistributions {
            modules("jdk.crypto.ec")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "OpenModInstaller"
            packageVersion = "1.0.0"
        }
    }
}
