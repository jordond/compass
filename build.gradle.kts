plugins {
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.poko) apply false
    alias(libs.plugins.publish) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.dependencies)
    alias(libs.plugins.binaryCompatibility)
    alias(libs.plugins.kotlinx.kover)
}

apiValidation {
    nonPublicMarkers += "dev.jordond.compass.InternalCompassApi"
    ignoredProjects += listOf("composeApp", "androidApp")
}

dependencies {
    subprojects
        .filter { it.name.startsWith("compass-") }
        .forEach { project ->
            dokka(project(project.path))
            kover(project(project.path))
        }
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(rootDir.resolve("dokka"))
    }
}
