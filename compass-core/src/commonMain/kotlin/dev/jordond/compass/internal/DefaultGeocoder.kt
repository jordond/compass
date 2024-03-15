package dev.jordond.compass.internal

import dev.jordond.compass.Geocoder
import dev.jordond.compass.LatLng
import dev.jordond.compass.Location
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class DefaultGeocoder(
    private val dispatcher: CoroutineDispatcher,
) : Geocoder {

    override fun available(): Boolean = geocoderAvailable()

    override suspend fun reverseGeocode(coordinates: LatLng): Geocoder.Result {
        try {
            val location = withContext(dispatcher) { coordinates.reverseGeocode() }
                ?: return Geocoder.Result.NotFound

            return Geocoder.Result.Success(location)
        } catch (cause: CancellationException) {
            throw cause
        } catch (cause: Throwable) {
            return when (cause) {
                is NotSupportedException -> Geocoder.Result.NotSupported
                is IllegalArgumentException -> Geocoder.Result.InvalidCoordinates
                else -> Geocoder.Result.GeocodeFailed(cause.message ?: "Unknown error")
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
 * @receiver The coordinates [LatLng] to geocode.
 * @return The address of the coordinates or `null` if the address could not be found.
 * @throws GeocodeError If an error occurred while geocoding.
 * @throws NotSupportedException if the device does not support geocoding.
 */
internal expect suspend fun LatLng.reverseGeocode(): Location?