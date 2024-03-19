@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.poko)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

kotlin {
    explicitApi()

    applyDefaultHierarchyTemplate()

    androidTarget {
        publishAllLibraryVariants()
    }

    jvm("desktop")

    // TODO: Waiting for kotest 5.9
//    wasmJs {
//        browser()
//    }

    js {
        browser()
    }

    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "compass-geocoding-api"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.compassCore)
            implementation(projects.compassGeocoderCore)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            api(libs.ktor.client.core)
            api(libs.ktor.client.contentNegotiation)
            api(libs.ktor.serialization.json)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotest.assertions)
        }

        androidMain.dependencies {
            api(libs.ktor.client.android)
        }

        appleMain.dependencies {
            api(libs.ktor.client.darwin)
        }

        jsMain.dependencies {
            api(libs.ktor.client.js)
        }
    }

    // https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations["main"].compilerOptions.options.freeCompilerArgs.add("-Xexport-kdoc")
    }

}

android {
    namespace = "dev.jordond.compass.geocoder.api"

    compileSdk = libs.versions.sdk.compile.get().toInt()
    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()
    }

    kotlin {
        jvmToolchain(jdkVersion = 17)
    }
}
