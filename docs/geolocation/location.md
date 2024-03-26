# 🌐 Location

The result from Geolocation operations will return a `Location` object, which looks like this:

```kotlin
class Location(
    public val coordinates: Coordinates,
    public val accuracy: Double,
    public val azimuth: Azimuth?,
    public val speed: Speed?,
    public val altitude: Altitude?,
    public val timestampMillis: Long,
)
```

You can read the [KDocs here](https://docs.compass.jordond.dev/compass-core/dev.jordond.compass/-location/index.html).

### Detailed location data

Depending on the `Priority` you use to request the location, determines what kind of data is present.

All location results will return the coordinates and the accuracy of those coordinates in meters. In order to get the other values you will need to use a more accurate `Priority`, like so:

```kotlin
val location: Location = geolocator.current(Priority.HighAccuracy)
```

There is still a chance those values will be `null`, since it is up to the device to determine what data is returned.

{% hint style="info" %}
On Android the `Priority.HighAccuracy` requires the `ACCESS_FINE_LOCATION` permission. This will be requested automatically by Compass.
{% endhint %}
