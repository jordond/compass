plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.bundles.logic.plugins)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "convention.multiplatform"
            implementationClass = "dev.jordond.compass.convention.plugin.KotlinMultiplatformConventionPlugin"
        }

        register("publish") {
            id = "convention.publish"
            implementationClass = "dev.jordond.compass.convention.plugin.PublishConventionPlugin"
        }
    }
}
