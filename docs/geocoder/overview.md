# 🌎 Overview

Geocoding is the process of turning coordinates into a Place (reverse), or turning an address name into coordinates.

Compass offers the following features:

* Reverse: Turn coordinates (latitude, longitude) into a Place object.
* Forward: Turn an address search query into coordinates.
* Android and iOS support, using built-in services.
* Support for web based API services.
  * Currently support for [Google Maps ](https://developers.google.com/maps/documentation/geocoding)and [Mapbox](https://docs.mapbox.com/#search) is included.
  * Easily create a wrapper for your own API.

{% hint style="info" %}
Built-in services are used for Android and iOS. That means that there is restrictions to its usage.  You can be throttled or blocked from using the geocoding services if you make too many requests in a short period of time.
{% endhint %}

