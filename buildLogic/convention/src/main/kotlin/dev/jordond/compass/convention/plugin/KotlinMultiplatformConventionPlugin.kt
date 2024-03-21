package dev.jordond.compass.convention.plugin

import com.android.build.gradle.LibraryExtension
import dev.jordond.compass.convention.Platform
import dev.jordond.compass.convention.Platforms
import dev.jordond.compass.convention.alias
import dev.jordond.compass.convention.configureKotlin
import dev.jordond.compass.convention.configureKotlinAndroid
import dev.jordond.compass.convention.libs
import dev.jordond.compass.convention.setNamespace
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

open class MultiPlatformConventionExtension {
    var isStatic: Boolean = true
    var isLibrary: Boolean = true
    var platforms: List<Platform> = Platforms.All
    internal var configureFramework: Framework.() -> Unit = {}

    fun add(platform: Platform) {
        platforms = platforms + platform
    }

    fun mobileOnly() {
        platforms = Platforms.Mobile
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

        alias("android-library")
        alias("multiplatform")
        alias("poko")

        if (extension.platforms.contains(Platform.Android)) {
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                setNamespace(project.name.replace("-", "."))
                println("Setting namespace: $namespace")
            }
        }

        project.afterEvaluate {
            extensions.configure<KotlinMultiplatformExtension> {
                applyDefaultHierarchyTemplate()

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

                if (extension.platforms.contains(Platform.Linux)) {
                    linuxX64()
                    linuxArm64()
                }

                // TODO: Waiting on kotest 5.9
//                if (extension.platforms.contains(Platform.Web)) {
//                    wasmJs {
//                        browser()
//                    }
//                }

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
                    implementation(libs.findLibrary("kotest-assertions").get())
                }
            }
        }

        configureKotlin()
    }

    companion object {

        private const val PLUGIN_NAME = "multiplatformConvention"
    }
}
