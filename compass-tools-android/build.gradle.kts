import dev.jordond.compass.convention.Platform
import dev.jordond.compass.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platform.Android)

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(projects.compassCore)
            implementation(libs.androidx.startup)
            implementation(libs.androidx.activity)
        }
    }
}