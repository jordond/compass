package dev.jordond.compass.geocoder.internal

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.GeocoderResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class DefaultGeocoder(
    private val dispatcher: CoroutineDispatcher,
) : Geocoder {

    override fun available(): Boolean = geocoderAvailable()

    override suspend fun places(coordinates: Location): GeocoderResult {
        try {
            val place = withContext(dispatcher) { coordinates.reverseGeocode() }
            if (place.isEmpty() || place.all { it.isEmpty }) {
                return GeocoderResult.NotFound
            }

            return GeocoderResult.Success(place)
        } catch (cause: CancellationException) {
            throw cause
        } catch (cause: Throwable) {
            return when (cause) {
                is NotSupportedException -> GeocoderResult.NotSupported
                is IllegalArgumentException -> GeocoderResult.InvalidCoordinates
                else -> GeocoderResult.GeocodeFailed(cause.message ?: "Unknown error")
            }
        }
    }
}

/**
 * Check if the device supports geocoding.
 *
 * @return `true` if the device supports geocoding, `false` otherwise.
 */
internal expect fun geocoderAvailable(): Boolean

/**
 * Geocode a location to an address.
 *
 * @receiver The coordinates [Location] to geocode.
 * @return The address of the coordinates or `null` if the address could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal expect suspend fun Location.reverseGeocode(): List<Place>