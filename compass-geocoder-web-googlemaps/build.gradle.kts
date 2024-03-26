plugins {
    alias(libs.plugins.convention.android)
    alias(libs.plugins.convention.multiplatform)
    alias(libs.plugins.convention.publish)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
            implementation(projects.compassGeocoder)
            api(projects.compassGeocoderWeb)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
