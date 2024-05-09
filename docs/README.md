# 🧭 Compass

[![Maven Central Version](https://img.shields.io/maven-central/v/dev.jordond.compass/compass-core)](https://central.sonatype.com/namespace/dev.jordond.compass)

## Overview

Compass is a Kotlin Multiplatform library location toolkit. It provides a set of tools for working with location data, including geocoding, reverse geocoding, and more. The library is built with a focus on simplicity and ease of use, providing a straightforward API for geocoding operations.

### Features

* [Broken link](broken-reference "mention")
  * Native support for Android and iOS
  * Support for other platforms by using web based APIs
    * Included support for [Google Maps ](https://developers.google.com/maps/documentation/geocoding)and [Mapbox](https://docs.mapbox.com/#search)
    * See [web-api-service](geocoding/web-api-service/ "mention")
* [Broken link](broken-reference "mention")
  * Mobile support (Android/iOS)
  * Browser [Geolocation API](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation\_API)
  * Built-in permission handling
* [Broken link](broken-reference "mention")
  * Mobile support (Android/iOS) via Geocoder
  * Support for other platforms by using web based APIs
    * Included support for [Google Maps ](https://developers.google.com/maps/documentation/geocoding)and [Mapbox](https://docs.mapbox.com/#search) via the Decoder API
    * Google Places API support is planned

### Easy to use

Compass has a simple API:

```kotlin
suspend fun lookupCoordinates(latitude: Double, longitude: Double): Place? {
    val geocoder = Geocoder()
    val result: GeocoderResult<Place> = geocoder.reverse(latitude, longitude)
    return result.getOrNull()
}
```

The above `Geocoder()` function is one of many extension functions included to make your life easier. Behind the scenes a `PlatformGeocoder` is used to do all the heavy lifting, and can be created and provided on a per-platform basis.

You can learn more about geocoding here: [overview.md](geocoder/overview.md "mention")

{% hint style="info" %}
The above `Geocoder()`extension function is from the Android/iOS only artifact. If you plan on supporting other platforms, check out [mixed-platforms.md](usage/mixed-platforms.md "mention").
{% endhint %}

### Get Started

Here is a few good starting points to start using Compass!

{% content-ref url="broken-reference" %}
[Broken link](broken-reference)
{% endcontent-ref %}

{% content-ref url="supported-platforms.md" %}
[supported-platforms.md](supported-platforms.md)
{% endcontent-ref %}

{% content-ref url="setup/add-dependencies.md" %}
[add-dependencies.md](setup/add-dependencies.md)
{% endcontent-ref %}
