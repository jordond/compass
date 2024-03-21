plugins {
    alias(libs.plugins.convention.multiplatform)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.compassCore)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
