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
     * @param location The [Location] to geocode.
     * @return A [GeocoderResult] containing the list of [Place]s or an error.
     */
    public suspend fun places(location: Location): GeocoderResult<Place>

    /**
     * Geocode a pair of coordinates to list of [Place]s.
     *
     * Valid coordinates are in the range of -90 to 90 for latitude and -180 to 180 for longitude.
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
     * **Note:** This function is not supported by iOS and will **always** return a failure result.
     *
     * @param address The address to geocode.
     * @return A [GeocoderResult] containing the list of [Place]s or an error.
     */
    public suspend fun places(address: String): GeocoderResult<Place>
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