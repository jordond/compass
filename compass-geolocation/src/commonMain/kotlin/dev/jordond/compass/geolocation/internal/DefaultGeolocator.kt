package dev.jordond.compass.geolocation.internal

import dev.jordond.compass.Location
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.Locator
import dev.jordond.compass.geolocation.Priority
import dev.jordond.compass.geolocation.exception.PermissionException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class DefaultGeolocator(
    override val locator: Locator,
    private val dispatcher: CoroutineDispatcher,
) : Geolocator {

    override fun isAvailable(): Boolean = locator.isAvailable()

    override suspend fun last(): GeolocatorResult = handleResult { locator.last() }

    override suspend fun current(priority: Priority): GeolocatorResult {
        return handleResult { locator.current(priority) }
    }

    override fun track(request: LocationRequest): Flow<Location> = locator.track(request)

    override fun stopTracking() = locator.stopTracking()

    private suspend fun handleResult(block: suspend () -> Location?): GeolocatorResult {
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
                is PermissionException -> GeolocatorResult.PermissionError(cause)
                is NotSupportedException -> GeolocatorResult.NotSupported
                else -> GeolocatorResult.GeolocationFailed(cause.message ?: "Unknown error")
            }
        }
    }
}