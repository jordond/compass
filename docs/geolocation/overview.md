# 📍 Overview

Geolocation includes the following features:

* Track a user's location
* Get the current location
* Customize the accuracy of the tracking
* Android and iOS support, using built-in services.
* Built-in Location permission handling

{% hint style="info" %}
Built-in services are used for Android and iOS. That means that there is restrictions to its usage.
{% endhint %}

## Quickstart

Include the required dependencies for your targets, see [add-dependencies.md](../setup/add-dependencies.md "mention").

#### Create the Geolocator

Creating a geolocator depends on your platform, but in the simplest use-case of an Android & iOS only targets. You can do something like this:

```kotlin
val geolocator: Geolocator = Geolocator.mobile()
```

#### Get current location

Getting the current location is simple, just call:

```kotlin
val result: GeolocatorResult = geolocator.current()
// Handle the result:

when (result) {
    is GeolocatorResult.Success -> {
        // Do something with result.location
    }
    is GeolocatorResult.Error -> when(result) {
        is GeolocatorResult.NotSupported -> TODO()
        is GeolocatorResult.NotFound -> TODO()
        is GeolocatorResult.PermissionError -> TODO()
        is GeolocatorResult.GeolocationFailed -> TODO()
    }
}
```

Or if you only care about the result:

```kotlin
val location: Location? = geolocator.currentOrNull()
```

{% hint style="info" %}
Compass will handle asking the user for while-in-use location permissions. If they deny the permission then the result will be `PermissionError.` If the user has denied the permission permanently, you will need to prompt the user to open the settings.
{% endhint %}

