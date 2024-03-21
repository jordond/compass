plugins {
    alias(libs.plugins.convention.multiplatform)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.compassCore)
            implementation(projects.compassGeocoder)

            implementation(libs.kermit)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
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
