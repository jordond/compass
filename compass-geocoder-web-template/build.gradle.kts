plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.poko)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
    alias(libs.plugins.kotlinx.serialization)
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
            baseName = "compass-geocoding-web-TEMPLATE"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
            api(projects.compassGeocoderCore)
            api(projects.compassGeocoderWeb)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotest.assertions)
        }
    }

    // https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations["main"].compilerOptions.options.freeCompilerArgs.add("-Xexport-kdoc")
    }

}

android {
    namespace = "dev.jordond.compass.geocoder.web.TEMPLATE"

    compileSdk = libs.versions.sdk.compile.get().toInt()
    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()
    }

    kotlin {
        jvmToolchain(jdkVersion = 17)
    }
}
