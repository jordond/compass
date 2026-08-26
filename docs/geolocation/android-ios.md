# 📱 Android / iOS

Compass supports Geolocation on Android and iOS by using the built in location services.

To use the Geolocator, it requires the user to provide permission. This is handled automatically when you start tracking or attempt to get the current location.

Make sure you read the [Android](https://developer.android.com/develop/sensors-and-location/location) and [iOS](https://developer.apple.com/documentation/corelocation) documentation to fully understand how the location services work.

{% hint style="info" %}
If your project supports both Mobile and other targets, you need to configure your setup to provide a `Geolocator` for each platform. Check out [mixed-platforms.md](../usage/mixed-platforms.md "mention")for more information.
{% endhint %}

## Usage

Follow these steps to create a `Geolocator`

### Create Locator

Geolocator is powered by the `Locator` object:

```kotlin
val locator: Locator = MobileLocator()
val locator: Locator = Locator.mobile()
```

### Create Geolocator

Then you can use that to create the `Geolocator` object:

```kotlin
val geolocator: Geolocator = Geolocator(locator)
```

Or you can use an extension function to skip the `Locator` step:

```kotlin
val geolocator: Geolocator = Geolocator()
val geolocator: Geolocator = MobileGeolocator()
val geolocator: Geolocator = Geolocator.mobile()
```

## Permissions

The user needs to grant permission in order to get location data.

On Android there are no further steps required, but on iOS you need to edit your `info.plist`, see [android-ios.md](../setup/android-ios.md "mention") to learn more.

When you attempt to access the location, Compass will automatically ask the user for permission.

### Precise and approximate location

A granted permission does not mean the app can read a precise location. The user can allow location while withholding precision, on Android by answering "Approximate" and on iOS by turning Precise Location off, and the fixes that follow are accurate to kilometres rather than metres. `Priority.HighAccuracy` does not override that, the system clamps the request.

`LocationPermissionController.grantedAccuracy()` reports which of the two the app currently holds. It reads the current state and never prompts.

```kotlin
val controller = LocationPermissionController.mobile()

when (controller.grantedAccuracy()) {
    LocationAccuracy.Full -> preselectTheNearestCafe()
    // Kilometre scale, so a 150m proximity check can never pass. Ask the user to
    // turn precision on in Settings instead of silently failing.
    LocationAccuracy.Reduced -> promptForPreciseLocation()
    LocationAccuracy.Unknown -> requestPermission()
}
```

{% hint style="warning" %}
The two platforms differ in what `priority` does when requesting. On Android `Priority.HighAccuracy` requests `ACCESS_FINE_LOCATION`, so `PermissionState.Granted` does mean precise location was granted. On iOS the priority is ignored, because `CLLocationManager` has no way to ask for precision up front, the user chooses it inside the one system prompt. `Granted` there means only that a location can be read. Use `grantedAccuracy()` rather than assuming.
{% endhint %}

## Google Play Services on Android

On Android there are two ways to read a location, and you can pick which one you want.

`geolocation-mobile` uses the built-in `LocationManager` and doesn't depend on Google Play Services. It works on every Android device, including ones without Play Services, and it's safe to use in a copyleft licensed app.

`geolocation-android-gms` adds the Play Services fused location provider, which combines GPS, Wi-Fi, cell and sensors to get better locations while using less battery. It pulls in the closed source `play-services-location` library.

### Using the fused provider

Add the artifact to your Android source set, that's the only step:

```kts
kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation("dev.jordond.compass:geolocation-android-gms:$compassVersion")
        }
    }
}
```

Your common code doesn't change. `MobileLocator()` and `Geolocator.mobile()` will prefer the fused provider, and fall back to `LocationManager` on devices where Play Services is missing or out of date:

```kotlin
// commonMain, unchanged
val geolocator: Geolocator = Geolocator.mobile()
```

If you want the fused provider and nothing else, so that a device without Play Services fails instead of falling back, the artifact also has Android-only entry points:

```kotlin
val locator: Locator = GmsLocator()
val locator: Locator = Locator.gms()

val geolocator: Geolocator = GmsGeolocator()
val geolocator: Geolocator = Geolocator.gms()

// Throws IllegalStateException when Play Services is unavailable, so check first
if (isPlayServicesAvailable()) Geolocator.gms() else Geolocator.mobile()
```

### Which one to use

* You want the best accuracy and battery usage, and depending on Play Services is fine, then add `geolocation-android-gms`.
* You can't have proprietary dependencies, maybe you're shipping to F-Droid or your app is GPL licensed, then you don't need to do anything since `geolocation-mobile` already covers you.
* You want to support devices without Play Services, but still get the fused locations where they exist, then add `geolocation-android-gms` and the fallback is handled for you.

{% hint style="warning" %}
Before Compass 4.0.0 the `geolocation-mobile` and `permissions-mobile` artifacts depended on `play-services-location` and always used the fused provider. They no longer do. To get the old behaviour back, add `geolocation-android-gms` to your Android source set. Without it your app keeps working, using the built-in `LocationManager`. See [4.0.0.md](../migration/4.0.0.md "mention").
{% endhint %}

### What changes without Play Services

Location still works, but the two sources aren't identical.

The accuracy will differ. On API 31 and above the platform has a fused provider of its own, which Compass uses, and it's close to the Play Services one. Below API 31 there is no fused source, so Compass picks GPS or network based on the request `priority`. Expect slower first locations and more variance there.

`isAvailable()` reports whether location services are turned on, while the Play Services version also checks the request settings.

The `interval` option is honoured by both, see below.

## Get location

Now you can follow the steps in [geolocator.md](geolocator.md "mention")

## Request options

`LocationRequest` carries more than the priority. Two of its options behave in ways worth knowing about on mobile.

### `interval`

The gap between location updates while tracking, defaulting to 5 seconds.

On Android this is passed to the location provider, and the minimum update interval is held to the same value. Without that the provider is free to deliver updates at twice the rate you asked for whenever another app is already using location. That applies to both the built-in `LocationManager` and the fused provider.

CoreLocation has no time based equivalent, it reports a fix whenever it has one, so on iOS the interval is applied to the updates as they come out. The first update after `track` always arrives, later ones are dropped until the interval has elapsed. The rate CoreLocation itself runs at is unchanged, so this costs no extra battery, and it does not make updates arrive any sooner than CoreLocation produces them.

```kotlin
// Roughly one update per second on both platforms
geolocator.track(LocationRequest(interval = 1_000))
```

{% hint style="warning" %}
Tracking again while tracking is already running applies the new `interval` but keeps the original `priority`. Call `stopTracking()` first if you need to change the priority.
{% endhint %}

### `maximumAge`

How old a cached location may be and still satisfy `current()`, in milliseconds. It defaults to `0`, which always requests a fresh fix.

Set it and `current()` will hand back the last known location when that location is young enough, which is immediate rather than the seconds a fresh fix takes. Age is the only criteria, matching the [W3C Geolocation API](https://developer.mozilla.org/en-US/docs/Web/API/PositionOptions/maximumAge) option it is modelled on, so a cached fix that is less accurate than you asked for still counts. If nothing suitable is cached, a fresh location is requested as usual.

```kotlin
// Reuse a fix from the last minute, otherwise go and get one
geolocator.current(LocationRequest(maximumAge = 60_000))
```
