package dev.jordond.compass.geocoder.internal

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.GeocoderResult
import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.geocoder.exception.NotSupportedException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class DefaultGeocoder(
    override val platformGeocoder: PlatformGeocoder,
    private val dispatcher: CoroutineDispatcher,
) : Geocoder {

    /**
     * @see Geocoder.isAvailable
     */
    override fun isAvailable(): Boolean = platformGeocoder.isAvailable()

    /**
     * @see Geocoder.places
     */
    override suspend fun places(latitude: Double, longitude: Double): GeocoderResult<Place> {
        return handleResult { platformGeocoder.placeFromLocation(latitude, longitude) }
    }

    /**
     * @see Geocoder.locations
     */
    override suspend fun locations(address: String): GeocoderResult<Location> {
        return handleResult { platformGeocoder.locationFromAddress(address) }
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