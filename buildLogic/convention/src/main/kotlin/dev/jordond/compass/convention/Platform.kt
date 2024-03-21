package dev.jordond.compass.convention

import dev.jordond.compass.convention.Platform.Android
import dev.jordond.compass.convention.Platform.Ios
import dev.jordond.compass.convention.Platform.Jvm
import dev.jordond.compass.convention.Platform.Linux
import dev.jordond.compass.convention.Platform.MacOS
import dev.jordond.compass.convention.Platform.Web

enum class Platform {
    Android,
    Ios,
    MacOS,
    Linux,
    Jvm,
    Web,
}

object Platforms {

    val All: List<Platform> = listOf(Android, Ios, MacOS, Linux, Jvm, Web)
    val Mobile: List<Platform> = listOf(Android, Ios)
    val Native: List<Platform> = listOf(MacOS, Linux)
}