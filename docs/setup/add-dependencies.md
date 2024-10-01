# 📇 All Dependencies

### Add Maven

If you don't already have Maven Central in your repositories yet, add the following:

```kts
// settings.gradle.kts

pluginManagement {
    repositories {
        mavenCentral()
        // etc
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        // etc
    }
}
```

### Add Compass dependencies

[![Maven Central Version](https://img.shields.io/maven-central/v/dev.jordond.compass/compass-core)](https://central.sonatype.com/namespace/dev.jordond.compass)

{% tabs %}
{% tab title="Version Catalog" %}
1. Declare the dependencies in your `libs.versions.toml`:

{% code fullWidth="false" %}
```toml
[versions]
compass = "1.2.2"

[libraries]
compass-autocomplete = { module = "dev.jordond.compass:autocomplete", version.ref = "compass" }
compass-autocomplete-mobile = { module = "dev.jordond.compass:autocomplete-mobile", version.ref = "compass" }
compass-autocomplete-web = { module = "dev.jordond.compass:autocomplete-web", version.ref = "compass" }
compass-autocomplete-geocoder-googlemaps = { module = "dev.jordond.compass:autocomplete-geocoder-googlemaps", version.ref = "compass" }
compass-autocomplete-geocoder-mapbox = { module = "dev.jordond.compass:autocomplete-geocoder-mapbox", version.ref = "compass" }
compass-geocoder = { module = "dev.jordond.compass:geocoder", version.ref = "compass" }
compass-geocoder-mobile = { module = "dev.jordond.compass:geocoder-mobile", version.ref = "compass" }
compass-geocoder-web = { module = "dev.jordond.compass:geocoder-web", version.ref = "compass" }
compass-geocoder-web-googlemaps = { module = "dev.jordond.compass:geocoder-web-googlemaps", version.ref = "compass" }
compass-geocoder-web-mapbox = { module = "dev.jordond.compass:geocoder-web-mapbox", version.ref = "compass" }
compass-geocoder-web-opencage = { module = "dev.jordond.compass:geocoder-web-opencage", version.ref = "compass" }
compass-geolocation = { module = "dev.jordond.compass:geolocation", version.ref = "compass" }
compass-geolocation-mobile = { module = "dev.jordond.compass:geolocation-mobile", version.ref = "compass" }
compass-geolocation-browser = { module = "dev.jordond.compass:geolocation-browser", version.ref = "compass" }
compass-permissions-mobile = { module = "dev.jordond.compass:permissions-mobile", version.ref = "compass" }
```
{% endcode %}

2. Add the dependencies to your `build.gradle.kts`:

{% code fullWidth="false" %}
```kts
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                // Geocoding
                implementation(libs.compass.geocoder)

                // To use geocoding you need to use one or more of the following

                // Optional - Geocoder support for only iOS and Android
                implementation(libs.compass.geocoder.mobile)

                // Optional - Geocoder support for all platforms, but requires an API key from the service
                implementation(libs.compass.geocoder.web.googlemaps)
                implementation(libs.compass.geocoder.web.mapbox)
                implementation(libs.compass.geocoder.web.opencage)

                // Optional - If you want to create your own geocoder implementation
                implementation(libs.compass.geocoder.web)
                
                // Geolocation
                implementation(libs.compass.geolocation)

                // To use geolocation you need to use one or more of the following
                
                // Optional - Geolocation support for only iOS and Android
                implementation(libs.compass.geolocation.mobile)
                
                // Optional - Geolocation support for JS/WASM Browser Geolocation API
                implementation(libs.compass.geolocation.browser)

                // Autocomplete
                implementation(libs.compass.autocomplete)

                // Optional - Autocomplete support for only iOS and Android using native Geocoder
                implementation(libs.compass.autocomplete.mobile)

                // Optional - Autocomplete support for all platforms, using services Geocoder APIs
                implementation(libs.compass.autocomplete.geocoder.googlemaps)
                implementation(libs.compass.autocomplete.geocoder.mapbox)

                // Optional - If you want to create your own geocoder implementation
                implementation(libs.compass.autocomplete.web)

                // Optional - Location permissions for mobile
                implementation(libs.compass.permissions.mobile)
            }
        }  
    }
}
```
{% endcode %}
{% endtab %}

{% tab title="Dependencies" %}
```kts
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                val compassVersion = "1.0.0"

                // Geocoding
                implementation("dev.jordond.compass:geocoder:$compassVersion")

                // To use geocoding you need to use one or more of the following

                // Optional - Geocoder support for only iOS and Android
                implementation("dev.jordond.compass:geocoder-mobile:$compassVersion")

                // Optional - Geocoder support for all platforms, but requires an API key from the service
                implementation("dev.jordond.compass:geocoder-web-googlemaps:$compassVersion")
                implementation("dev.jordond.compass:geocoder-web-mapbox:$compassVersion")
                implementation("dev.jordond.compass:geocoder-web-opencage:$compassVersion")

                // Optional - If you want to create your own geocoder implementation
                implementation("dev.jordond.compass:geocoder-web:$compassVersion")
                
                // Geolocation
                implementation("dev.jordond.compass:geolocation:$compassVersion")

                // To use geolocation you need to use one or more of the following
                
                // Optional - Geolocation support for only iOS and Android
                implementation("dev.jordond.compass:geolocation-mobile:$compassVersion")
                
                // Optional - Geolocation support for JS/WASM Browser Geolocation API
                implementation("dev.jordond.compass:geolocation-browser:$compassVersion")

                // Autocomplete
                implementation("dev.jordond.compass:autocomplete:$compassVersion")

                // Optional - Autocomplete support for only iOS and Android using native Geocoder
                implementation("dev.jordond.compass:autocomplete-mobile:$compassVersion")

                // Optional - Autocomplete support for all platforms, using services Geocoder APIs
                implementation("dev.jordond.compass:autocomplete-geocoder-googlemaps:$compassVersion")
                implementation("dev.jordond.compass:autocomplete-geocoder-mapbox:$compassVersion")

                // Optional - If you want to create your own geocoder implementation
                implementation("dev.jordond.compass:autocomplete-web:$compassVersion")

                // Optional - Location permissions for mobile (Android/iOS)
                implementation("dev.jordond.compass:permissions-mobile:$compassVersion")
            }
        }
    }
}
```
{% endtab %}
{% endtabs %}

{% hint style="info" %}
If you plan on using Compass in a project that targets both mobile and non-mobile platforms (desktop, browser, etc). Then you will need to make sure use `expect/actual` to provide the implementation for each platform. See [mixed-platforms.md](../usage/mixed-platforms.md "mention").
{% endhint %}
