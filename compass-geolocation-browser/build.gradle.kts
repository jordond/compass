import dev.jordond.compass.convention.Platforms
import dev.jordond.compass.convention.configureMultiplatform

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform(Platforms.Browser)

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
            implementation(projects.compassGeolocation)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}