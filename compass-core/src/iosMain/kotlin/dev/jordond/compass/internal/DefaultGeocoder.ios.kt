package dev.jordond.compass.internal

import dev.jordond.compass.LatLng
import dev.jordond.compass.Location

/**
 * Check if the device supports geocoding.
 *
 * @return `true` if the device supports geocoding, `false` otherwise.
 */
internal actual fun geocoderAvailable(): Boolean {
    TODO("Not yet implemented")
}

/**
 * Geocode a location to an address.
 *
 * @receiver The coordinates [LatLng] to geocode.
 * @return The address of the coordinates or `null` if the address could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal actual suspend fun LatLng.reverseGeocode(): Location? {
    TODO("Not yet implemented")
}
