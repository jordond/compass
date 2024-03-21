plugins {
    alias(libs.plugins.convention.multiplatform)
    alias(libs.plugins.convention.publish)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
            api(projects.compassGeocoderCore)
            api(projects.compassGeocoderWeb)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
