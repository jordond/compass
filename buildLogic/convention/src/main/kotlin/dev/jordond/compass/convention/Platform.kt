package dev.jordond.compass.convention

import dev.jordond.compass.convention.Platform.Android
import dev.jordond.compass.convention.Platform.Ios
import dev.jordond.compass.convention.Platform.Js
import dev.jordond.compass.convention.Platform.Jvm
import dev.jordond.compass.convention.Platform.Linux
import dev.jordond.compass.convention.Platform.MacOS
import dev.jordond.compass.convention.Platform.NodeJs
import dev.jordond.compass.convention.Platform.Wasm

enum class Platform {
    Android,
    Ios,
    MacOS,
    Linux,
    Jvm,
    Js,
    Wasm,
    NodeJs,
}

object Platforms {

    val All: List<Platform> = listOf(Android, Ios, MacOS, Linux, Jvm, Js, Wasm, NodeJs)
    val Mobile: List<Platform> = listOf(Android, Ios)
    val Browser: List<Platform> = listOf(Js, Wasm)
    val Compose: List<Platform> = listOf(Android, Ios, Jvm, Js, Wasm)
}