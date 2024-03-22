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
            implementation("androidx.activity:activity-ktx:1.8.2")

        }

        iosMain.dependencies {
            implementation(libs.kotlinx.atomicfu)
        }
    }
}