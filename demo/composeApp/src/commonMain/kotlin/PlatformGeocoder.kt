import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.geocoder.web.GoogleMapsPlatformGeocoder
import dev.jordond.compass.geocoder.web.HttpApiPlatformGeocoder
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsLocationType

fun createGeocoder(apiKey: String? = null): Geocoder {
    val platformGeocoder =
        if (apiKey.isNullOrBlank()) getPlatformGeocoder()
//        else MapBoxPlatformGeocoder(apiKey)
        else GoogleMapsPlatformGeocoder(
            apiKey = apiKey,
            client = HttpApiPlatformGeocoder.httpClient(enableLogging = true)
        ) {
            locationType(GoogleMapsLocationType.Approximate)
        }
    return Geocoder(platformGeocoder)
}

expect fun getPlatformGeocoder(): PlatformGeocoder