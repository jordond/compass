import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    applyDefaultHierarchyTemplate()

    (this as ExtensionAware)
        .extensions
        .findByType(KotlinMultiplatformAndroidLibraryTarget::class.java)
        ?.apply {
            namespace = "dev.jordond.compass.demo"
            compileSdk = libs.versions.sdk.compile.get().toInt()
            minSdk = libs.versions.sdk.min.get().toInt()
            compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
        }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("composeApp")
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    jvm("desktop")

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
        }

        commonMain.dependencies {
            implementation(projects.compassCore)
            implementation(projects.compassGeocoder)
            implementation(projects.compassGeocoderWebMapbox)
            implementation(projects.compassGeocoderWebGooglemaps)
            implementation(projects.compassGeocoderWebOpencage)
            implementation(projects.compassGeolocation)
            implementation(projects.compassAutocomplete)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.compose.runtime)
            implementation(libs.compose.resources)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.kermit)
            implementation(libs.bundles.voyager)
            implementation(libs.bundles.stateHolder)
        }

        val desktopMain = getByName("desktopMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        val mobileMain = create("mobileMain") {
            dependsOn(commonMain.get())
            androidMain.get().dependsOn(this)
            iosMain.get().dependsOn(this)
            dependencies {
                implementation(projects.compassGeocoderMobile)
                implementation(projects.compassGeolocationMobile)
                implementation(projects.compassPermissionsMobile)
                implementation(projects.compassAutocompleteMobile)
            }
        }

        val wasmJsMain = getByName("wasmJsMain") {
            dependencies {
                implementation(projects.compassGeolocationBrowser)
            }
        }

        val nonMobileMain = create("nonMobileMain") {
            dependsOn(commonMain.get())
            desktopMain.dependsOn(this)
            wasmJsMain.dependsOn(this)
            dependencies {
                implementation(projects.compassGeocoderWebGooglemaps)
                implementation(projects.compassAutocompleteGeocoderGooglemaps)
            }
        }

        val nonBrowser = create("nonBrowser") {
            dependsOn(commonMain.get())
            desktopMain.dependsOn(this)
            androidMain.get().dependsOn(this)
            iosMain.get().dependsOn(this)
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.jordond.compass.demo"
            packageVersion = "1.0.0"
        }
    }
}
