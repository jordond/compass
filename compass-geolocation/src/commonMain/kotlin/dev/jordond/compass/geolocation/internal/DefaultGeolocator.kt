package dev.jordond.compass.geolocation.internal

import dev.jordond.compass.ExtendedLocation
import dev.jordond.compass.Location
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.Locator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class DefaultGeolocator(
    private val locator: Locator,
    private val dispatcher: CoroutineDispatcher,
) : Geolocator {

    override fun isAvailable(): Boolean = locator.isAvailable()

    override suspend fun last(): GeolocatorResult<Location> = handleResult { locator.last() }

    override suspend fun current(): GeolocatorResult<Location> = handleResult { locator.current() }

    override suspend fun extended(): GeolocatorResult<ExtendedLocation> {
        return handleResult { locator.extended() }
    }

    override fun track(): Flow<Location> = locator.track()

    override fun trackExtended(): Flow<ExtendedLocation> = locator.trackExtended()

    override fun stopTracking() = locator.stopTracking()

    private suspend fun <T> handleResult(block: suspend () -> T?): GeolocatorResult<T> {
        try {
            if (!isAvailable()) {
                return GeolocatorResult.NotSupported
            }
            val result = withContext(dispatcher) { block() }
            if (result == null) {
                return GeolocatorResult.NotFound
            }

            return GeolocatorResult.Success(result)
        } catch (cause: CancellationException) {
            throw cause
        } catch (cause: Throwable) {
            return when (cause) {
                // TODO: Handle permissions error
                is NotSupportedException -> GeolocatorResult.NotSupported
                else -> GeolocatorResult.GeolocationFailed(cause.message ?: "Unknown error")
            }
        }
    }
}