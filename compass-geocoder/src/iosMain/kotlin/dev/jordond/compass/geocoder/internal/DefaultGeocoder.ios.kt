package dev.jordond.compass.geocoder.internal

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLPlacemark
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * iOS always supports geocoding.
 */
internal actual fun geocoderAvailable(): Boolean = true

/**
 * Geocode a location to an address.
 *
 * @receiver The coordinates [Location] to geocode.
 * @return The address of the coordinates or `null` if the address could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal actual suspend fun Location.reverseGeocode(): Place? {
    val geocoder = CLGeocoder()
    val location = CLLocation(latitude, longitude)

    @Suppress("UNCHECKED_CAST")
    return suspendCancellableCoroutine { continuation ->
        geocoder.reverseGeocodeLocation(location) { result, error ->
            when {
                error != null ->
                    continuation.resumeWithException(GeocodeError(error.localizedDescription))
                result.isNullOrEmpty() -> continuation.resume(null)
                else -> {
                    val places = (result as? List<CLPlacemark>)?.map { it.toPlace() }
                    continuation.resume(places?.firstOrNull())
                }
            }
        }

        continuation.invokeOnCancellation {
            geocoder.cancelGeocode()
        }
    }
}
