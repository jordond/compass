package dev.jordond.compass.geocoder

import dev.jordond.compass.Location
import dev.jordond.compass.Place

/**
 * Geocode a [Location] to a list of [Place]s or `null` if unsuccessful.
 *
 * In most cases, the list will contain a single [Place]. However, in some cases, it may return
 * multiple [Place]s.
 *
 * @receiver The [Geocoder] to use for geocoding.
 * @param location The [Location] to geocode.
 * @return A list of [Place]s or `null` if the geocoding was unsuccessful, either due to a geocoder
 * error or not being able to find the location.
 */
public suspend fun Geocoder.placesOrNull(location: Location): List<Place>? =
    places(location).getOrNull()

/**
 * Geocode a pair of coordinates to a list of [Place]s or `null` if unsuccessful.
 *
 * In most cases, the list will contain a single [Place]. However, in some cases, it may return
 * multiple [Place]s.
 *
 * @receiver The [Geocoder] to use for geocoding.
 * @param latitude The latitude of the coordinates.
 * @param longitude The longitude of the coordinates.
 * @return A list of [Place]s or `null` if the geocoding was unsuccessful, either due to a geocoder
 * error or not being able to find the location.
 */
public suspend fun Geocoder.placesOrNull(latitude: Double, longitude: Double): List<Place>? =
    places(latitude, longitude).getOrNull()

/**
 * Geocode a [Location] to a [Place] or `null` if unsuccessful.
 *
 * @receiver The [Geocoder] to use for geocoding.
 * @param location The address to geocode.
 * @return A [Place] or `null` if the geocoding was unsuccessful, either due to a geocoder error or
 * not being able to find the location.
 */
public suspend fun Geocoder.placeOrNull(location: Location): Place? =
    places(location).getFirstOrNull()

/**
 * Geocode a pair of coordinates to a [Place] or `null` if unsuccessful.
 *
 * @receiver The [Geocoder] to use for geocoding.
 * @param latitude The latitude of the coordinates.
 * @param longitude The longitude of the coordinates.
 * @return A [Place] or `null` if the geocoding was unsuccessful, either due to a geocoder error or
 * not being able to find the location.
 */
public suspend fun Geocoder.placeOrNull(latitude: Double, longitude: Double): Place? =
    places(latitude, longitude).getFirstOrNull()

/**
 * Get a list of [Location]s for a given address or `null` if unsuccessful.
 *
 * In most cases, the list will contain a single [Location]. However, in some cases, it may
 * return multiple [Location]s.
 *
 * @param address The address to geocode.
 * @return A list of [Location]s or `null` if the geocoding was unsuccessful, either due to a
 * geocoder error or not being able to find the location.
 */
public suspend fun Geocoder.locationsOrNull(address: String): List<Location>? =
    locations(address).getOrNull()?.toList()

/**
 * Get a [Location] for a given address or `null` if unsuccessful.
 *
 * @param address The address to geocode.
 * @return A [Location] or `null` if the geocoding was unsuccessful, either due to a geocoder error
 * or not being able to find the location.
 */
public suspend fun Geocoder.locationOrNull(address: String): Location? =
    locations(address).getFirstOrNull()