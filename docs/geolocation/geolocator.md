# 🔍 Geolocator

The `compass-geolocator` artifact provides an interface `Geolocator` that has members for:

* Getting the current location
* Starting and stopping tracking the location
* Check if location services are available

## Creating a Geolocator

To get an instance of `Geolocator` you can use the factory function:

```kotlin
public fun Geolocator(
    locator: Locator,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator
```

### Locator

A `Locator` is interface that can be implemented to provide all of the geolocation functionality.

Compass provides `Locator`'s for [android-ios.md](android-ios.md "mention") and the [browser.md](browser.md "mention"):

```kotlin
// Android and iOS
val locator = Locator.mobile()

// Or for the browser
val locator = Locator.browser()

// Create the geolocator
val geolocator: Geolocator = Geolocator(locator)
```

There are also included extension functions for each artifact:

```kotlin
// Android and iOS
val geolocator: Geolocator = Geolocator(MobileLocator)
val geolocator: Geolocator = MobileGeolocator()
val geolocator: Geolocator = Geolocator.mobile()
```

There are similar extension functions for the [browser.md](browser.md "mention")artifact.

{% hint style="info" %}
Make sure you add the proper dependencies, see [add-dependencies.md](../setup/add-dependencies.md "mention").
{% endhint %}

### Custom Locator

If you would like to provide your own you can implement the object yourself:

```kotlin
val locator = object : Locator {
    // implement Locator
}
val geolocator: Geolocator = Geolocator(locator)
```

## Permissions

Compass will handle the permissions for Android and iOS automatically for you, see [android-ios.md](android-ios.md "mention").

## Current location

To get the current location of the device call `Geolocator.current()`. It will return a `GeolocatorResult`, or you can use one of the extension functions:

```kotlin
val location: Location? = geolocator.current().getOrNull()
val location: Location? = geolocator.currentOrNull()
```

### GeolocatorResult

When you call `Geolocator.current()` you will receive a `GeolocationResult.` It can be one of the following:

* `GeolocatorResult.Success`
  * This has contains a `Location` object of the current location details.
* `GeolocatorResult.Error`
  * `NotSupported`: Geolocation is not supported on this device
  * `GeolocationFailed`: Geolocation failed for an unknown reason
  * `PermissionError`: We don't have permission to use the geolocation services

## Tracking location

You can subscribe to location updates by collecting the `Geolocator.locationUpdates`, or to get updates to the status of tracking (errors, permissions, updates) use `Geolocator.trackingStatus`:

```kotlin
val geolocator: Geolocator

val status = geolocator.trackingStatus.map { status ->
    when (status) {
        is TrackingStatus.Idle -> println("Not Tracking")
        is TrackingStatus.Update -> printLn(status.location.coordinates)
        is TrackingStatus.Error -> {
            val error: GeolocatorResult.Error = status.cause

            // Show the permissions settings screen
            val permissionDeniedForever = error.isPermissionDeniedForever
            
            // Handle other errors
            when (error) {
                // TODO
            }
        }
    }
}

suspend fun start() {
    geolocator.startTracking()
}

suspend fun stop() {
    geolocator.stopTracking()
}
```

## Location details

You can read about what kind of data is in the `Location` object by reading [location.md](location.md "mention").
