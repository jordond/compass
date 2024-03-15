package dev.jordond.compass.geocoder.internal

import android.location.Geocoder
import android.os.Build
import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.internal.context.ContextProvider

private const val MAX_RESULTS = 5

/**
 * Geocode a [Location] to a [Place].
 *
 * @receiver The coordinates [Location] to geocode.
 * @return The address of the coordinates or `null` if the address could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal actual suspend fun Location.reverseGeocode(): List<Place> {
    val geocoder = createGeocoder()
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        @Suppress("DEPRECATION")
        syncOperation { geocoder.getFromLocation(latitude, longitude, MAX_RESULTS) }
    } else {
        asyncOperation { listener ->
            geocoder.getFromLocation(latitude, longitude, MAX_RESULTS, listener)
        }
    }
}

/**
 * Geocode an address to a [Place].
 *
 * @receiver The address to geocode.
 * @return The address of the coordinates or `null` if the address could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal actual suspend fun String.reverseGeocode(): List<Place> {
    val geocoder = createGeocoder()
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        @Suppress("DEPRECATION")
        syncOperation { geocoder.getFromLocationName(this, MAX_RESULTS) }
    } else {
        asyncOperation { listener ->
            geocoder.getFromLocationName(this, MAX_RESULTS, listener)
        }
    }
}

private fun createGeocoder(): Geocoder {
    if (Geocoder.isPresent().not()) throw NotSupportedException()
    return Geocoder(ContextProvider.getInstance().context)
}
