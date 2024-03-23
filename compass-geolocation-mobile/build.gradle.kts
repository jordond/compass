plugins {
    alias(libs.plugins.convention.multiplatform)
    alias(libs.plugins.convention.publish)
}

multiplatformConvention {
    mobileOnly()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
            api(projects.compassGeolocation)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kermit)
        }

        androidMain.dependencies {
            implementation(projects.compassToolsAndroid)
            api(libs.play.services.location)
            implementation(libs.androidx.activity)
            implementation(libs.androidx.startup)
        }

        iosMain.dependencies {
            implementation(libs.kotlinx.atomicfu)
        }
    }
}