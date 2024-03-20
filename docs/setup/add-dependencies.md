# 📇 Add Dependencies

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

{% tabs %}
{% tab title="Version Catalog" %}

1. Declare the dependencies in your `libs.versions.toml`:

{% code fullWidth="false" %}
```toml
[versions]
compass = "1.0.0"

[libraries]
compass-geocoder = { module = "dev.jordond.compass:compass-geocoder", version.ref = "compass" }
compass-geocoder-mobile = { module = "dev.jordond.compass:compass-geocoder-mobile", version.ref = "compass" }
compass-geocoder-web = { module = "dev.jordond.compass:compass-geocoder-web", version.ref = "compass" }
compass-geocoder-web-googlemaps = { module = "dev.jordond.compass:compass-geocoder-googlemaps", version.ref = "compass" }
compass-geocoder-web-mapbox = { module = "dev.jordond.compass:compass-geocoder-mapbox", version.ref = "compass" }
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

                // Optional - Support for only iOS and Android
                implementation(libs.compass.geocoder.mobile)

                // Optional - Support for all platforms, but requires an API key from the service
                implementation(libs.compass.geocoder.web.googlemaps)
                implementation(libs.compass.geocoder.web.mapbox)

                // Optional - If you want to create your own geocoder implementation
                implementation(libs.compass.geocoder.web)
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
                implementation("dev.jordond.compass:compass-geocoder:$compassVersion")

                // To use geocoding you need to use one or more of the following

                // Optional - Support for only iOS and Android
                implementation("dev.jordond.compass:compass-geocoder-mobile:$compassVersion")

                // Optional - Support for all platforms, but requires an API key from the service
                implementation("dev.jordond.compass:compass-geocoder-web-googlemaps:$compassVersion")
                implementation("dev.jordond.compass:compass-geocoder-web-mapbox:$compassVersion")

                // Optional - If you want to create your own geocoder implementation
                implementation("dev.jordond.compass:compass-geocoder-web:$compassVersion")
            }
        }
    }
}
```
{% endtab %}
{% endtabs %}

{% hint style="info" %}
If you plan on using Compass Geocoder in a project that targets both mobile and non-mobile platforms (desktop, browser, etc). Then you will need to make sure use `expect/actual` to provide a `PlatformGeocoder` object. See [mixed-platforms.md](../usage/mixed-platforms.md "mention")
{% endhint %}
