package dev.jordond.compass.geocoder

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place

/**
 * Geocode a [Coordinates] to a list of [Place]s or `null` if unsuccessful.
 *
 * In most cases, the list will contain a single [Place]. However, in some cases, it may return
 * multiple [Place]s.
 *
 * @receiver The [Geocoder] to use for geocoding.
 * @param coordinates The [Coordinates] to geocode.
 * @return A list of [Place]s or `null` if the geocoding was unsuccessful, either due to a geocoder
 * error or not being able to find the location.
 */
public suspend fun Geocoder.placesOrNull(coordinates: Coordinates): List<Place>? =
    reverse(coordinates).getOrNull()

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
    reverse(latitude, longitude).getOrNull()

/**
 * Geocode a [Coordinates] to a [Place] or `null` if unsuccessful.
 *
 * @receiver The [Geocoder] to use for geocoding.
 * @param coordinates The address to geocode.
 * @return A [Place] or `null` if the geocoding was unsuccessful, either due to a geocoder error or
 * not being able to find the location.
 */
public suspend fun Geocoder.placeOrNull(coordinates: Coordinates): Place? =
    reverse(coordinates).getFirstOrNull()

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
    reverse(latitude, longitude).getFirstOrNull()

/**
 * Get a list of [Coordinates]s for a given address or `null` if unsuccessful.
 *
 * In most cases, the list will contain a single [Coordinates]. However, in some cases, it may
 * return multiple [Coordinates]s.
 *
 * @param address The address to geocode.
 * @return A list of [Coordinates]s or `null` if the geocoding was unsuccessful, either due to a
 * geocoder error or not being able to find the location.
 */
public suspend fun Geocoder.coordinatesListOrNull(address: String): List<Coordinates>? =
    forward(address).getOrNull()?.toList()

/**
 * Get a [Coordinates] for a given address or `null` if unsuccessful.
 *
 * @param address The address to geocode.
 * @return A [Coordinates] or `null` if the geocoding was unsuccessful, either due to a geocoder error
 * or not being able to find the location.
 */
public suspend fun Geocoder.coordinatesOrNull(address: String): Coordinates? =
    forward(address).getFirstOrNull()