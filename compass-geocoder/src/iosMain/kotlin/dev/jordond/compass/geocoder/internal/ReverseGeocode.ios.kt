package dev.jordond.compass.geocoder.internal

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import platform.CoreLocation.CLLocation

/**
 * Geocode a [Location] to a [Place].
 *
 * @receiver The coordinates [Location] to geocode.
 * @return The address of the coordinates or `null` if the address could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal actual suspend fun Location.reverseGeocode(): List<Place> {
    val location = CLLocation(latitude, longitude)
    return geocodeOperation { listener ->
        reverseGeocodeLocation(location, listener)
    }.toPlaces()
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
    return geocodeOperation { listener ->
        geocodeAddressString(this@reverseGeocode, listener)
    }.toPlaces()
}