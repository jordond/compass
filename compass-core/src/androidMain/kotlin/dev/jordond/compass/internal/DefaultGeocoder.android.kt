package dev.jordond.compass.internal

import android.annotation.TargetApi
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.internal.context.ContextProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Geocode a location to an address.
 *
 * @receiver The coordinates [Location] to geocode.
 * @return The address of the coordinates or `null` if the address could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal actual suspend fun Location.reverseGeocode(): Place? {
    if (Geocoder.isPresent().not()) throw NotSupportedException()
    val geocoder = Geocoder(ContextProvider.getInstance().context)

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        geocoder.deprecatedReverseGeocode(this)
    } else {
        geocoder.reverseGeocode(this)
    }
}

@Suppress("DEPRECATION")
private suspend fun Geocoder.deprecatedReverseGeocode(location: Location): Place? {
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

    return result.getOrNull()?.firstOrNull()?.toPlace()
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
private suspend fun Geocoder.reverseGeocode(location: Location): Place? {
    return suspendCoroutine { continuation ->
        getFromLocation(
            /* latitude = */ location.latitude,
            /* longitude = */ location.longitude,
            /* maxResults = */ 1,
            object : GeocodeListener {

                override fun onGeocode(addresses: MutableList<Address>) {
                    if (addresses.isEmpty()) return continuation.resume(null)

                    continuation.resume(addresses.first().toPlace())
                }

                override fun onError(errorMessage: String?) {
                    continuation.resumeWithException(GeocodeError(errorMessage))
                }
            },
        )
    }
}
