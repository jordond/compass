package dev.jordond.compass.geocoder.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.GeocoderResult
import dev.jordond.compass.geocoder.PlatformGeocoder
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
     * @see Geocoder.reverse
     */
    override suspend fun reverse(latitude: Double, longitude: Double): GeocoderResult<Place> {
        return handleResult(invalidArgument = GeocoderResult.InvalidCoordinates) {
            platformGeocoder.reverse(latitude, longitude)
        }
    }

    /**
     * @see Geocoder.forward
     */
    override suspend fun forward(address: String): GeocoderResult<Coordinates> {
        return handleResult {
            platformGeocoder.forward(address)
        }
    }

    /**
     * @param invalidArgument What an [IllegalArgumentException] from the platform means, when it
     * means something more specific than a failed lookup.
     */
    private suspend fun <T> handleResult(
        invalidArgument: GeocoderResult.Error? = null,
        block: suspend () -> List<T>,
    ): GeocoderResult<T> {
        try {
            if (!isAvailable()) {
                return GeocoderResult.NotSupported
            }
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
                is NotFoundException -> GeocoderResult.NotFound
                is IllegalArgumentException -> invalidArgument ?: cause.toFailed()
                else -> cause.toFailed()
            }
        }
    }

    private fun Throwable.toFailed(): GeocoderResult.Error =
        GeocoderResult.GeocodeFailed(message ?: "Unknown error")
}
