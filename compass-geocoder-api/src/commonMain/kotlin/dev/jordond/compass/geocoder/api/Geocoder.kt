package dev.jordond.compass.geocoder.api

import dev.jordond.compass.geocoder.Geocoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Create a new [Geocoder] instance for geocoding operations.
 *
 * @param dispatcher The [CoroutineDispatcher] to use for geocoding operations.
 * @return A new [Geocoder] instance.
 */
public fun Geocoder(
    httpGeocoderApi: HttpGeocoderApi,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = Geocoder(httpGeocoderApi, dispatcher)