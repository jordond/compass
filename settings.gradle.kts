enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        google()
        mavenCentral()
    }

    includeBuild("buildLogic")
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }
}

plugins {
    id("com.gradle.develocity") version "3.17.6"
}

develocity {
    buildScan {
        publishing.onlyIf { context ->
            context.buildResult.failures.isNotEmpty() && !System.getenv("CI").isNullOrEmpty()
        }
    }
}

rootProject.name = "compass"

include(
    ":compass-core",
    ":compass-autocomplete",
    ":compass-autocomplete-geocoder-googlemaps",
    ":compass-autocomplete-geocoder-mapbox",
    ":compass-autocomplete-mobile",
    ":compass-autocomplete-web",
    ":compass-geocoder",
    ":compass-geocoder-mobile",
    ":compass-geocoder-web",
    ":compass-geocoder-web-googlemaps",
    ":compass-geocoder-web-mapbox",
    ":compass-geocoder-web-opencage",
    ":compass-geocoder-web-template",
    ":compass-geolocation",
    ":compass-geolocation-mobile",
    ":compass-geolocation-browser",
    ":compass-permissions",
    ":compass-permissions-mobile",
    ":compass-tools-android",
    ":compass-tools-web",
)

include(":demo:composeApp")
