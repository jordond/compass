package dev.jordond.compass.internal

import android.annotation.TargetApi
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import dev.jordond.compass.LatLng
import dev.jordond.compass.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Geocode a location to an address.
 *
 * @receiver The coordinates [LatLng] to geocode.
 * @return The address of the coordinates or `null` if the address could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal actual suspend fun LatLng.reverseGeocode(): Location? {
    if (Geocoder.isPresent().not()) throw NotSupportedException()
    val geocoder = Geocoder(ContextProvider.getInstance().context)

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        geocoder.reverseGeocode(this)
    } else {
        geocoder.reverseGeocodeSdk33(this)
    }
}

@Suppress("DEPRECATION")
private suspend fun Geocoder.reverseGeocode(location: LatLng): Location? {
    val result: Result<List<Address>?> = withContext(Dispatchers.IO) {
        runCatching {
            getFromLocation(
                /* latitude = */ location.latitude,
                /* longitude = */ location.longitude,
                /* maxResults = */ 1,
            )?.toList()
        }
    }

    val exception = result.exceptionOrNull()
    if (exception != null) throw GeocodeError(exception.message)

    return result.getOrNull()?.firstOrNull()?.toLocation()
}

/**
 * Check if the device supports geocoding.
 *
 * @return `true` if the device supports geocoding, `false` otherwise.
 */
internal actual fun geocoderAvailable(): Boolean {
    return Geocoder.isPresent()
}

@TargetApi(Build.VERSION_CODES.TIRAMISU)
private suspend fun Geocoder.reverseGeocodeSdk33(location: LatLng): Location? {
    return suspendCoroutine { continuation ->
        getFromLocation(
            /* latitude = */ location.latitude,
            /* longitude = */ location.longitude,
            /* maxResults = */ 1,
            object : GeocodeListener {

                override fun onGeocode(addresses: MutableList<Address>) {
                    if (addresses.isEmpty()) return continuation.resume(null)

                    continuation.resume(addresses.first().toLocation())
                }

                override fun onError(errorMessage: String?) {
                    continuation.resumeWithException(GeocodeError(errorMessage))
                }
            },
        )
    }
}
