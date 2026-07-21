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

## Get location

Now you can follow the steps in [geolocator.md](geolocator.md "mention")

## Request options

`LocationRequest` carries more than the priority. Two of its options behave in ways worth knowing about on mobile.

### `interval`

The gap between location updates while tracking, defaulting to 5 seconds.

Android passes this to the fused location provider, which also holds the minimum update interval to the same value. Without that the provider is free to deliver at twice the requested rate whenever another app is already driving location.

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
