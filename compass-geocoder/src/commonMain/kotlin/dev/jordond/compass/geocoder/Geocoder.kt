package dev.jordond.compass.geocoder

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.internal.DefaultGeocoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Provides geocoding operations:
 *
 * - Convert an address to a latitude and longitude (forward geocoding)
 * - Convert a latitude and longitude to an address (reverse geocoding)
 */
public interface Geocoder {

    /**
     * Whether or not the geocoder is available.
     *
     * **Note:** On iOS this will **always** return `true`.
     *
     * @return `true` if the geocoder is available, `false` otherwise.
     */
    public fun available(): Boolean

    /**
     * Geocode a [Location] object to a list of [Place]s.
     *
     * In most cases, the list will contain a single [Place]. However, in some cases, it may
     * return multiple [Place]s.
     *
     * @param location The [Location] to geocode.
     * @return A [GeocoderResult] containing the list of [Place]s or an error.
     */
    public suspend fun places(location: Location): GeocoderResult<Place>

    /**
     * Geocode a pair of coordinates to list of [Place]s.
     *
     * Valid coordinates are in the range of -90 to 90 for latitude and -180 to 180 for longitude.
     *
     * In most cases, the list will contain a single [Place]. However, in some cases, it may
     * return multiple [Place]s.
     *
     * @param latitude The latitude of the coordinates.
     * @param longitude The longitude of the coordinates.
     * @return A [GeocoderResult] containing the list of [Place]s or an error.
     */
    public suspend fun places(latitude: Double, longitude: Double): GeocoderResult<Place> =
        places(Location(latitude, longitude))

    /**
     * Geocode an address to a list of [Place].
     *
     * In most cases, the list will contain a single [Place]. However, in some cases, it may
     * return multiple [Place]s.
     *
     * @param address The address to geocode.
     * @return A [GeocoderResult] containing the list of [Place]s or an error.
     */
    public suspend fun places(address: String): GeocoderResult<Place>

    /**
     * Get a list of [Location]s for a given address.
     *
     * In most cases, the list will contain a single [Location]. However, in some cases, it may
     * return multiple [Location]s.
     *
     * @param address The address to geocode.
     * @return A [GeocoderResult] containing the list of [Location]s or an error.
     */
    public suspend fun locations(address: String): GeocoderResult<Location>

    public companion object
}

/**
 * Create a new [Geocoder] instance for geocoding operations.
 *
 * @param dispatcher The [CoroutineDispatcher] to use for geocoding operations.
 * @return A new [Geocoder] instance.
 */
public fun Geocoder(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = DefaultGeocoder(dispatcher)