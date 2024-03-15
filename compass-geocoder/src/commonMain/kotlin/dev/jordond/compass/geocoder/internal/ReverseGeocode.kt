package dev.jordond.compass.geocoder.internal

import dev.jordond.compass.Location
import dev.jordond.compass.Place

/**
 * Geocode a [Location] to a [Place].
 *
 * @receiver The coordinates [Location] to geocode.
 * @return The address of the coordinates or `null` if the address could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal expect suspend fun Location.reverseGeocode(): List<Place>

/**
 * Geocode an address to a [Place].
 *
 * @receiver The address to geocode.
 * @return The address of the coordinates or `null` if the address could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal expect suspend fun String.reverseGeocode(): List<Place>