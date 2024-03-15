package dev.jordond.compass.geocoder.internal

import dev.jordond.compass.Location

/**
 * Geocode an address name to a list of coordinate [Location]s.
 *
 * @receiver The address to geocode.
 * @return The [Location] of the address, or an empty list if the coordinates could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal expect suspend fun String.forwardGeocode(): List<Location>