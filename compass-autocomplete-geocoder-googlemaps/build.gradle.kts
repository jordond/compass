import dev.jordond.compass.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.poko)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform()

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
            api(projects.compassToolsWeb)
            implementation(projects.compassAutocomplete)
            api(projects.compassGeocoderWebGooglemaps)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}