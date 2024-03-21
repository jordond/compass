plugins {
    alias(libs.plugins.convention.multiplatform)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
        }
    }
}