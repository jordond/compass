package dev.jordond.compass.geocoder

import dev.jordond.compass.Location
import dev.jordond.compass.Place

/**
 * Geocode a [Location] to a list of [Place]s or `null` if unsuccessful.
 *
 * @receiver The [Geocoder] to use for geocoding.
 * @param location The [Location] to geocode.
 * @return A list of [Place]s or `null` if the geocoding was unsuccessful, either due to a geocoder
 * error or not being able to find the location.
 */
public suspend fun Geocoder.placesOrNull(location: Location): List<Place>? =
    places(location).locationsOrNull()

/**
 * Geocode a pair of coordinates to a list of [Place]s or `null` if unsuccessful.
 *
 * @receiver The [Geocoder] to use for geocoding.
 * @param latitude The latitude of the coordinates.
 * @param longitude The longitude of the coordinates.
 * @return A list of [Place]s or `null` if the geocoding was unsuccessful, either due to a geocoder
 * error or not being able to find the location.
 */
public suspend fun Geocoder.placesOrNull(latitude: Double, longitude: Double): List<Place>? =
    places(latitude, longitude).locationsOrNull()

/**
 * Geocode a [Location] to a [Place] or `null` if unsuccessful.
 *
 * @receiver The [Geocoder] to use for geocoding.
 * @param location The address to geocode.
 * @return A [Place] or `null` if the geocoding was unsuccessful, either due to a geocoder error or
 * not being able to find the location.
 */
public suspend fun Geocoder.placeOrNull(location: Location): Place? =
    places(location).locationsOrNull()?.firstOrNull()

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
    places(latitude, longitude).locationsOrNull()?.firstOrNull()