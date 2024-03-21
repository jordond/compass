package dev.jordond.compass.geolocation.mobile

import dev.jordond.compass.geolocation.Geolocator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Create a new [Geolocator] instance for geolocation operations.
 *
 * @param dispatcher The [CoroutineDispatcher] to use for geolocation operations.
 * @return A new [Geolocator] instance.
 */
public fun Geolocator(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = MobileGeolocator(dispatcher)

/**
 * Create a new [Geolocator] instance for geolocation operations.
 *
 * @param dispatcher The [CoroutineDispatcher] to use for geolocation operations.
 * @return A new [Geolocator] instance.
 */
@Suppress("FunctionName")
public fun MobileGeolocator(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = Geolocator(MobileLocator(), dispatcher)