import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.geocoder.mobile.MobilePlatformGeocoder

actual fun getPlatformGeocoder(): PlatformGeocoder {
    return MobilePlatformGeocoder()
}