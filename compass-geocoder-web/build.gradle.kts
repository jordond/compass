import dev.jordond.compass.convention.configureMultiplatform

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.poko)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform()

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.compassCore)
            api(projects.compassToolsWeb)
            implementation(projects.compassGeocoder)

            implementation(libs.kermit)
            implementation(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.serialization.json)
            api(libs.ktor.client.core)
            api(libs.ktor.client.contentNegotiation)
            api(libs.ktor.client.logging)
            api(libs.ktor.serialization.json)
        }

        androidMain.dependencies {
            api(libs.ktor.client.android)
        }

        appleMain.dependencies {
            api(libs.ktor.client.darwin)
        }

        jsMain.dependencies {
            api(libs.ktor.client.js)
        }

        jvmMain.dependencies {
            api(libs.ktor.client.okhttp)
        }
    }
}
