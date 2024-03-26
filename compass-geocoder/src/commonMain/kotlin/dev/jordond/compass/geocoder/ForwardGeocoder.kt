package dev.jordond.compass.geocoder

import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.internal.DefaultGeocoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public interface ForwardGeocoder {

    /**
     * Get a list of [Coordinates]s for a given address.
     *
     * In most cases, the list will contain a single [Coordinates]. However, in some cases, it may
     * return multiple [Coordinates]s.
     *
     * @param address The address to geocode.
     * @return A [GeocoderResult] containing the list of [Coordinates]s or an error.
     */
    public suspend fun forward(address: String): GeocoderResult<Coordinates>

    /**
     * Get a list of [Coordinates]s for a given address.
     *
     * In most cases, the list will contain a single [Coordinates]. However, in some cases, it may
     * return multiple [Coordinates]s.
     *
     * @param address The address to geocode.
     * @return A [GeocoderResult] containing the list of [Coordinates]s or an error.
     */
    public suspend fun locations(address: String): GeocoderResult<Coordinates> = forward(address)
}

/**
 * Create a new [ForwardGeocoder] using the provided [PlatformGeocoder].
 *
 * @param platformGeocoder The [PlatformGeocoder] to use for geocoding.
 * @param dispatcher The [CoroutineDispatcher] to use for running the geocoding operations.
 * @return A new [ForwardGeocoder].
 */
public fun ForwardGeocoder(
    platformGeocoder: PlatformGeocoder,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): ForwardGeocoder = DefaultGeocoder(platformGeocoder, dispatcher)
