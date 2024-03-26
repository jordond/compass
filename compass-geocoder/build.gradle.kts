plugins {
    alias(libs.plugins.convention.android)
    alias(libs.plugins.convention.multiplatform)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
