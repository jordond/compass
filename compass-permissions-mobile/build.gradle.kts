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
            api(projects.compassPermissions)
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            implementation(projects.compassToolsAndroid)
            api(libs.play.services.location)
            implementation(libs.androidx.activity)
            implementation(libs.androidx.fragment)
            implementation(libs.androidx.startup)

        }

        iosMain.dependencies {
            implementation(libs.kotlinx.atomicfu)
        }
    }
}