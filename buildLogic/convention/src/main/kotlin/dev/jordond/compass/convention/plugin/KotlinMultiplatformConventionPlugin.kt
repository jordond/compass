package dev.jordond.compass.convention.plugin

import dev.jordond.compass.convention.Platform
import dev.jordond.compass.convention.Platforms
import dev.jordond.compass.convention.alias
import dev.jordond.compass.convention.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

@Suppress("unused")
open class MultiPlatformConventionExtension {
    var isStatic: Boolean = true
    var isLibrary: Boolean = true
    var platforms: List<Platform> = Platforms.All
    internal var configureFramework: Framework.() -> Unit = {}

    var platform: Platform
        get() = platforms.first()
        set(value) {
            platforms = listOf(value)
        }

    fun add(platform: Platform) {
        platforms = platforms + platform
    }

    fun mobileOnly() {
        platforms = Platforms.Mobile
    }

    fun allComposePlatforms() {
        platforms = Platforms.Compose
    }

    fun configureFramework(block: Framework.() -> Unit) {
        configureFramework = block
    }
}

class KotlinMultiplatformConventionPlugin : Plugin<Project> {

    @ExperimentalWasmDsl
    override fun apply(target: Project) = with(target) {
        val extension = project.extensions
            .create<MultiPlatformConventionExtension>(PLUGIN_NAME)

        alias("multiplatform")
        alias("poko")

        project.afterEvaluate {
            extensions.configure<KotlinMultiplatformExtension> {
                applyDefaultHierarchyTemplate()

                @OptIn(ExperimentalKotlinGradlePluginApi::class)
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }

                if (extension.platforms.contains(Platform.Android)) {
                    androidTarget {
                        publishLibraryVariants("debug", "release")
                    }
                }

                if (extension.platforms.contains(Platform.Jvm)) {
                    jvm()
                }

                if (extension.platforms.contains(Platform.MacOS)) {
                    macosX64()
                    macosArm64()
                }

                // TODO: Waiting on ktor to have stable wasm support
//                if (extension.platforms.contains(Platform.Linux)) {
//                    linuxX64()
//                    linuxArm64()
//                }

                if (extension.platforms.contains(Platform.Js)) {
                    js {
                        browser()

                        if (extension.platforms.contains(Platform.NodeJs)) {
                            nodejs()
                        }
                    }
                }

                if (extension.platforms.contains(Platform.Wasm)) {
                    wasmJs {
                        browser()

                        if (extension.platforms.contains(Platform.NodeJs)) {
                            nodejs()
                        }
                    }
                }

                if (extension.platforms.contains(Platform.Ios)) {
                    listOf(
                        iosX64(),
                        iosArm64(),
                        iosSimulatorArm64()
                    ).forEach { target ->
                        target.binaries.framework {
                            baseName = project.name
                            isStatic = extension.isStatic
                            println("Configuring [${target.name}]: $baseName")
                            extension.configureFramework(this)
                        }
                    }
                }

                if (extension.isLibrary) {
                    // https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
                    targets.withType(KotlinNativeTarget::class.java) {
                        compilations["main"].compilerOptions.options.freeCompilerArgs.add("-Xexport-kdoc")
                    }
                }

                sourceSets.commonTest.dependencies {
                    implementation(kotlin("test"))
                    // TODO: Waiting on kotest 5.9 for wasm
//                    implementation(libs.findLibrary("kotest-assertions").get())
                }
            }
        }

        configureKotlin()
    }

    companion object {

        private const val PLUGIN_NAME = "multiplatformConvention"
    }
}
