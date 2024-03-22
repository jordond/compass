import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.PlatformGeocoder

/**
 * If an API key is provided, create a platform geocoder which falls back to an HTTP Geocoder
 * with the API key..
 *
 * If it is not provided it will create a platform geocoder.
 */
fun createGeocoder(apiKey: String? = null): Geocoder {
    val platformGeocoder =
        if (apiKey == null) getPlatformGeocoder()
        else getPlatformGeocoderOrFallback(apiKey)
    return Geocoder(platformGeocoder)
}

expect fun getPlatformGeocoder(): PlatformGeocoder

expect fun getPlatformGeocoderOrFallback(apiKey: String): PlatformGeocoder