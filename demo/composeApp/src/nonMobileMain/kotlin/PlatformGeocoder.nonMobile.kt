import dev.jordond.compass.geocoder.NotSupportedPlatformGeocoder
import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.geocoder.web.GoogleMapsPlatformGeocoder
import dev.jordond.compass.geocoder.web.HttpApiPlatformGeocoder
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsLocationType

/**
 * Normally you would use a custom [PlatformGeocoder] here or a [HttpApiPlatformGeocoder]. But in
 * this demo that is handled by the different screens that demonstrate different geocoder APIs.
 */
actual fun getPlatformGeocoder(): PlatformGeocoder {
    return NotSupportedPlatformGeocoder
}

actual fun getPlatformGeocoderOrFallback(apiKey: String): PlatformGeocoder {
    return GoogleMapsPlatformGeocoder(
        apiKey = apiKey,
        client = HttpApiPlatformGeocoder.httpClient(enableLogging = true)
    ) {
        locationType(GoogleMapsLocationType.Approximate)
    }
}