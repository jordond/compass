import dev.jordond.compass.convention.Platforms
import dev.jordond.compass.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.Mobile)

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
            implementation(projects.compassGeocoder)
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            implementation(projects.compassToolsAndroid)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.androidx.activity)
        }
    }
}
