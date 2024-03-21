package dev.jordond.compass.geocoder.mobile

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
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = MobileGeocoder(dispatcher)

/**
 * Create a new [Geocoder] instance for geocoding operations.
 *
 * @param dispatcher The [CoroutineDispatcher] to use for geocoding operations.
 * @return A new [Geocoder] instance.
 */
@Suppress("FunctionName")
public fun MobileGeocoder(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = Geocoder(MobilePlatformGeocoder(), dispatcher)
