import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.geocoder.web.MapBoxPlatformGeocoder

fun createGeocoder(apiKey: String? = null): Geocoder {
    val platformGeocoder =
        if (apiKey.isNullOrBlank()) getPlatformGeocoder()
        else MapBoxPlatformGeocoder(apiKey)
    return Geocoder(platformGeocoder)
}

expect fun getPlatformGeocoder(): PlatformGeocoder