package dev.jordond.compass.geocoder

import dev.jordond.compass.geocoder.internal.DefaultGeocoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Provides geocoding operations:
 *
 * - Convert an address to a latitude and longitude (forward geocoding)
 * - Convert a latitude and longitude to an address (reverse geocoding)
 */
public interface Geocoder : ForwardGeocoder, ReverseGeocoder {

    public val platformGeocoder: PlatformGeocoder

    /**
     * Check if the geocoder is available.
     *
     * @return `true` if the geocoder is available, `false` otherwise.
     */
    public fun isAvailable(): Boolean

    public companion object
}

/**
 * Create a new [Geocoder] instance for geocoding operations.
 *
 * @param platformGeocoder The platform-specific geocoder to use.
 * @param dispatcher The [CoroutineDispatcher] to use for geocoding operations.
 * @return A new [Geocoder] instance.
 */
public fun Geocoder(
    platformGeocoder: PlatformGeocoder,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = DefaultGeocoder(platformGeocoder, dispatcher)