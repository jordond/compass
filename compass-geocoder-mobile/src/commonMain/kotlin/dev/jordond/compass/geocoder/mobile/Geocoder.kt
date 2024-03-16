package dev.jordond.compass.geocoder.mobile

import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.PlatformGeocoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Create a new [Geocoder] instance for geocoding operations.
 *
 * @param dispatcher The [CoroutineDispatcher] to use for geocoding operations.
 * @return A new [Geocoder] instance.
 */
public fun Geocoder(
    geocoder: PlatformGeocoder = PlatformGeocoder.create(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = Geocoder(geocoder, dispatcher)

/**
 * Create an Android/iOS [PlatformGeocoder] instance for geocoding operations.
 *
 * @return A new Android/iOS [PlatformGeocoder] instance.
 */
public fun PlatformGeocoder.Companion.create(): PlatformGeocoder = createPlatformGeocoder()

internal expect fun createPlatformGeocoder(): PlatformGeocoder
