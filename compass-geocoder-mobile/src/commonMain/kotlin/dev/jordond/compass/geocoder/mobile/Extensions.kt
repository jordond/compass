package dev.jordond.compass.geocoder.mobile

import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.Geocoder

/**
 * Get a list of [Place]s for a given address or `null` if unsuccessful.
 *
 * In most cases, the list will contain a single [Place]. However, in some cases, it may return
 * multiple [Place]s.
 *
 * @receiver The [Geocoder] to use for geocoding.
 * @param address The address to geocode.
 * @return A list of [Place]s or `null` if the geocoding was unsuccessful, either due to a geocoder
 * error or not being able to find the location.
 */
public suspend fun Geocoder.placesOrNull(address: String): List<Place>? {
    return (platformGeocoder as? MobilePlatformGeocoder)?.placeFromAddress(address)
}