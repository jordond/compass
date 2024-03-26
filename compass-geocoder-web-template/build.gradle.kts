plugins {
    alias(libs.plugins.convention.android)
    alias(libs.plugins.convention.multiplatform)
    // Uncomment this line to enable publishing
//    alias(libs.plugins.convention.publish)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
            api(projects.compassGeocoder)
            api(projects.compassGeocoderWeb)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
