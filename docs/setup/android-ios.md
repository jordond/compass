# 🤖 Android / iOS

### Dependencies

If you only need to support mobile (Android and iOS) add the following dependencies:

```toml
# gradle/libs.versions.toml

[libraries]
compass-geocoder = { module = "dev.jordond.compass:geocoder", version.ref = "compass" }
compass-geocoder-mobile = { module = "dev.jordond.compass:geocoder-mobile", version.ref = "compass" }
compass-geolocation = { module = "dev.jordond.compass:geolocation", version.ref = "compass" }
compass-geolocation-mobile = { module = "dev.jordond.compass:geolocation-mobile", version.ref = "compass" }
```

Then you can add them to your `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                // Geocoding
                implementation(libs.compass.geocoder)
                implementation(libs.compass.geocoder.mobile)
                
                // Geolocation
                implementation(libs.compass.geolocation)
                implementation(libs.compass.geolocation.mobile)
            }
        }  
    }
}
```

### Geocoding Setup

No location permissions are needed for geocoding operations. Compass will use the built-in Android or iOS services. These services can be rate-limited, so make sure you aren't calling them too frequently. Check out the [Android documentation](https://developer.android.com/reference/android/location/Geocoder) as well as the [iOS documentation](https://developer.apple.com/documentation/corelocation/clgeocoder) for more information.

### Geolocation setup

Location permissions are required for using geolocation on Android and iOS. Compass can handle the permission requesting automatically for you.

#### Android

On Android you don't need to declare any location permission in your manifest since Compass includes those in the `geolocation-mobile` artifact.

#### iOS

On iOS you are required to edit your `info.plist` and add the following entries:

```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>Add a description for why you need this permission</string>
<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>Add a description for why you need this permission</string>
```

{% hint style="info" %}
Make sure you change the description for both permissions. You are required to explain to your users why you need their location. Check out the [Documentation](https://developer.apple.com/documentation/corelocation/requesting\_authorization\_to\_use\_location\_services#3385302\)).
{% endhint %}

If you don't add these keys to your `info.plist`, you will encounter a runtime exception when requesting permissions.
