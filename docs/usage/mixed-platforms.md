# 📱 Mixed platforms

If you are targeting Mobile (Android/iOS) and other platforms like Desktop, or the Browser. You will need to do some additional setup since the Mobile Geocoder/Geolocation services aren't available.

## Geocoding example

This example is for a Compose Multiplatform app that targets mobile and Desktop, using Google Maps on non-mobile platforms. This is just an example, be sure to customize to your needs.

### Declare Dependencies

First you will need to add the necessary dependencies:

```toml
# gradle/libs.versions.toml

[libraries]
compass-geocoder = { module = "dev.jordond.compass:compass-geocoder", version.ref = "compass" }
compass-geocoder-mobile = { module = "dev.jordond.compass:compass-geocoder-mobile", version.ref = "compass" }
compass-geocoder-web = { module = "dev.jordond.compass:compass-geocoder-web", version.ref = "compass" }
compass-geocoder-web-googlemaps = { module = "dev.jordond.compass:compass-geocoder-googlemaps", version.ref = "compass" }
compass-geocoder-web-mapbox = { module = "dev.jordond.compass:compass-geocoder-mapbox", version.ref = "compass" }
```

{% hint style="info" %}
Compass includes out of the box support for [Google Maps](https://developers.google.com/maps/documentation/geocoding/overview) and [Mapbox](https://docs.mapbox.com/api/search/geocoding-v6/). If you need support for another service you can use the `compass-geocoder-web` to implement your own, or [request-a-geocoder-api.md](../geocoding/request-a-geocoder-api.md "mention")
{% endhint %}

### Add dependencies & create source-set

Now you need to edit the `build.gradle.kts` for your application module and add the following dependencies to the proper source-sets.

{% hint style="info" %}
We will be creating a `mobileMain` and `nonMobileMain` source-set to minimize boilerplate. You can read about source-sets [here](https://kotlinlang.org/docs/multiplatform-advanced-project-structure.html#declaring-custom-source-sets).
{% endhint %}

```kts
// composeApp/build.gradle.kts

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compass.geocoder)
            implementation(libs.compass.geocoder.web.googlemaps)
        }
        
        val mobileMain by creating {
            dependsOn(commonMain.get())
            androidMain.get().dependsOn(this)
            iosMain.get().dependsOn(this)
            dependencies {
                implementation(libs.compass.geocoder.mobile)
            }
        }
        
        val desktopMain by getting
        val nonMobileMain by creating {
            dependsOn(commonMain.get())
            desktopMain.dependsOn(this)
            dependencies {
                implementation(libs.compass.geocoder.web.googlemaps)
            }
        }
    }
}
```

### Add platform expect/actual

This will produce the following folder structure, which you can then define a `expect` function to create a `Geocoder` instance:

```
composeApp
|
|----commonMain
|        |---kotlin/src/Platform.kt
|
|----mobileMain
|        |---kotlin/src/Platform.mobileMain.kt
|
|----nonMobileMain
|        |---kotlin/src/Platform.nonMobileMain.kt
```

Inside of `commonMain/kotlin/src/Platform.kt` you need to define an `expect` function that creates a `Geocoder` instance:

```kotlin
expect fun createGeocoder(): Geocoder
```

Then you need to create the `actual` functions:

```kotlin
// mobileMain/kotlin/src/Platform.mobileMain.kt

actual fun createGeocoder(): Geocoder {
    return Geocoder.mobile()
}
```

And for the `nonMobile` source set:

```kotlin
// mobileMain/kotlin/src/Platform.nonMobileMain.kt

actual fun createGeocoder(): Geocoder {
    // There are other optional parameters to customize the API request
    return Geocoder.googleMaps(apiKey = "my-google-maps-api-key")
}
```

{% hint style="info" %}
The Google Maps and Mapbox integration require API keys in order to use. If you want to customize the `HttpClient` or the arguments passed to the API service, you can use the optional parameters. See [web-api-service](../geocoding/web-api-service/ "mention").
{% endhint %}

### Using the Geocoder

Now that you are all setup you can use your `createGeocoder()` function in the `commonMain` source-set:

```kotlin
class MyRepository {
    private val geocoder: Geocoder = createGeocoder()
    
    suspend fun reverseGeocode(lat: Long, lng: Long): Location? {
        return geocoder.reverse(lat, lng).getOrNull()
    }
}
```

