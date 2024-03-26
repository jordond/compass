plugins {
    alias(libs.plugins.convention.android)
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
            implementation(projects.compassGeocoder)
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            implementation(projects.compassToolsAndroid)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.androidx.activity)
        }
    }
}
