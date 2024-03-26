import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.geocoder.mobile.MobilePlatformGeocoder
import dev.jordond.compass.geolocation.Locator
import dev.jordond.compass.geolocation.mobile.mobile

actual fun getPlatformGeocoder(): PlatformGeocoder = getPlatformGeocoderOrFallback("")

actual fun getPlatformGeocoderOrFallback(apiKey: String): PlatformGeocoder {
    return MobilePlatformGeocoder()
}

actual fun getPlatformLocator(handlePermissions: Boolean): Locator {
    return Locator.mobile(handlePermissions)
}