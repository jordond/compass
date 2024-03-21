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
            api(projects.compassGeolocation )
        }

        androidMain.dependencies {
            implementation(projects.compassToolsAndroid)
            api(libs.play.services.location)
        }
    }
}