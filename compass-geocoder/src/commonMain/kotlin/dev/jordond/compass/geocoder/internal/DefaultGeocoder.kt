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

    /**
     * @see Geocoder.available
     */
    override fun available(): Boolean = geocoderAvailable()

    /**
     * @see Geocoder.places
     */
    override suspend fun places(location: Location): GeocoderResult<Place> {
        return handleResult { location.reverseGeocode() }
    }

    /**
     * @see Geocoder.places
     */
    override suspend fun places(address: String): GeocoderResult<Place> {
        return handleResult { address.reverseGeocode() }
    }

    private suspend fun <T> handleResult(block: suspend () -> List<T>): GeocoderResult<T> {
        try {
            val place = withContext(dispatcher) { block() }
            if (place.isEmpty()) {
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