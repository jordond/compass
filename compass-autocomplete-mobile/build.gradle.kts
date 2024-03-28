import dev.jordond.compass.convention.Platforms
import dev.jordond.compass.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.poko)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.Mobile)

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
            implementation(projects.compassAutocomplete)
            implementation(projects.compassGeocoder)
            implementation(projects.compassGeocoderMobile)

            implementation(libs.kotlinx.coroutines.core)
        }
    }
}