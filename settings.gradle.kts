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
    id("com.gradle.enterprise") version "3.17"
}

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
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
