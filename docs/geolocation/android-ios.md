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
