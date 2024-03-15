package dev.jordond.compass.geocoder.internal

import android.os.Build
import dev.jordond.compass.Location

/**
 * Geocode an address name to a list of coordinate [Location]s.
 *
 * @receiver The address to geocode.
 * @return The [Location] of the address, or an empty list if the coordinates could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal actual suspend fun String.forwardGeocode(): List<Location> {
    val geocoder = createGeocoder()
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        @Suppress("DEPRECATION")
        syncOperation {
            geocoder.getFromLocationName(this, MAX_RESULTS)
        }.toLocations()
    } else {
        asyncOperation { listener ->
            geocoder.getFromLocationName(this, MAX_RESULTS, listener)
        }.toLocations()
    }
}