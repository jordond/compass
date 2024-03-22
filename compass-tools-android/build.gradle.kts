import dev.jordond.compass.convention.Platform

plugins {
    alias(libs.plugins.convention.multiplatform)
    alias(libs.plugins.convention.publish)
}

multiplatformConvention {
    platform = Platform.Android
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.startup)
            implementation(libs.androidx.activity)
        }
    }
}