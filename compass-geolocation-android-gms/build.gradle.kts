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
            api(projects.compassCore)
            api(projects.compassGeolocation)
            api(projects.compassGeolocationMobile)
            implementation(projects.compassPermissions)
            implementation(projects.compassPermissionsMobile)
            implementation(projects.compassToolsAndroid)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.core)
            implementation(libs.androidx.startup)

            implementation(libs.play.services.location)
            implementation(libs.play.services.coroutines)
        }
    }
}
