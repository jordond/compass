import dev.jordond.compass.convention.configureMultiplatform
import dev.jordond.compass.convention.dependencies

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.poko)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
    alias(libs.plugins.convention.multiplatform)
}

configureMultiplatform {
    commonMain.dependencies {
        implementation(libs.kotlinx.coroutines.core)
    }
}