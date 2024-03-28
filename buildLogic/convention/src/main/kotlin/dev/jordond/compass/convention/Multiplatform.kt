@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

package dev.jordond.compass.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

fun Project.configureMultiplatform(platform: Platform) {
    configureMultiplatform(listOf(platform))
}

fun Project.configureMultiplatform(
    platforms: List<Platform> = Platforms.All,
) {
    extensions.configure<KotlinMultiplatformExtension> {
        configureKotlin()
        configurePlatforms(platforms)
    }

    if (platforms.contains(Platform.Android)) {
        configureAndroid()
    }
}

internal fun KotlinMultiplatformExtension.configurePlatforms(
    platforms: List<Platform> = Platforms.All,
) {
    applyDefaultHierarchyTemplate()

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.add("dev.jordond.compass.InternalCompassApi")
    }

    if (platforms.contains(Platform.Android)) {
        androidTarget {
            publishAllLibraryVariants()
        }
    }

    if (platforms.contains(Platform.Jvm)) {
        jvm()
    }

    if (platforms.contains(Platform.MacOS)) {
        macosX64()
        macosArm64()
    }

    // TODO: Waiting on ktor to have stable wasm support
//                if (platforms.contains(Platform.Linux)) {
//                    linuxX64()
//                    linuxArm64()
//                }

    if (platforms.contains(Platform.Js)) {
        js {
            browser()

            if (platforms.contains(Platform.NodeJs)) {
                nodejs()
            }
        }
    }

    if (platforms.contains(Platform.Wasm)) {
        wasmJs {
            browser()

            if (platforms.contains(Platform.NodeJs)) {
                nodejs()
            }
        }
    }

    if (platforms.contains(Platform.Ios)) {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { target ->
            target.binaries.framework {
                baseName = project.name
                isStatic = true
            }
        }
    }

    // https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    this.targets.withType(KotlinNativeTarget::class.java) {
        compilations["main"].compilerOptions.options.freeCompilerArgs.add("-Xexport-kdoc")
    }

    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
        // TODO: Waiting on kotest 5.9 for wasm
//                    implementation(libs.findLibrary("kotest-assertions").get())
    }
}