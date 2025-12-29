plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    compileOnly(libs.bundles.logic.plugins)
}

gradlePlugin {
    plugins {
        register("multiplatformConvention") {
            id = "convention.multiplatform"
            implementationClass = "dev.jordond.compass.convention.plugin.MultiplatformConventionPlugin"
        }
    }
}
