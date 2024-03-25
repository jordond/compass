import dev.jordond.compass.convention.Platforms

plugins {
    alias(libs.plugins.convention.multiplatform)
    alias(libs.plugins.convention.publish)
}

multiplatformConvention {
    platforms = Platforms.Browser
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
            implementation(projects.compassGeolocation)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.kermit)
        }
    }
}