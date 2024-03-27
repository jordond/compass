package dev.jordond.compass.geolocation.internal

import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.Locator
import dev.jordond.compass.geolocation.TrackingStatus
import dev.jordond.compass.permissions.exception.PermissionDeniedException
import dev.jordond.compass.permissions.exception.PermissionDeniedForeverException
import dev.jordond.compass.permissions.exception.PermissionException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class DefaultGeolocator(
    internal val locator: Locator,
    private val dispatcher: CoroutineDispatcher,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher),
) : Geolocator {

    private val status = MutableStateFlow<TrackingStatus>(TrackingStatus.Idle)
    private var trackingJob: Job? = null

    init {
        coroutineScope.launch {
            locator.locationUpdates.collect { value ->
                status.update { TrackingStatus.Update(value) }
            }
        }
    }

    override val trackingStatus: Flow<TrackingStatus> = status

    override suspend fun isAvailable(): Boolean = withContext(dispatcher) {
        locator.isAvailable()
    }

    override suspend fun current(priority: Priority): GeolocatorResult {
        return handleResult { locator.current(priority) }
    }

    override fun track(request: LocationRequest): Flow<TrackingStatus> = status.also {
        if (trackingJob?.isActive == true) return@also

        trackingJob = coroutineScope.launch {
            if (!isAvailable()) {
                status.update { TrackingStatus.Error(GeolocatorResult.NotSupported) }
            } else {
                status.update { TrackingStatus.Tracking }

                try {
                    locator.track(request).launchIn(this)
                } catch (cause: Throwable) {
                    if (cause is CancellationException) throw cause
                    status.update { TrackingStatus.Error(cause.toResult()) }
                }
            }
        }
    }

    override fun stopTracking() {
        locator.stopTracking()
        status.update { TrackingStatus.Idle }
        trackingJob?.cancel()
        trackingJob = null
    }

    private fun Throwable.toResult(): GeolocatorResult.Error = when (this) {
        is CancellationException -> throw this
        is PermissionException -> when (this) {
            is PermissionDeniedException -> GeolocatorResult.PermissionDenied(false)
            is PermissionDeniedForeverException -> GeolocatorResult.PermissionDenied(true)
            else -> GeolocatorResult.PermissionError(this)
        }
        is NotSupportedException -> GeolocatorResult.NotSupported
        is NotFoundException -> GeolocatorResult.NotFound
        else -> GeolocatorResult.GeolocationFailed(this.message ?: "Unknown error")
    }

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
        } catch (cause: Throwable) {
            return cause.toResult()
        }
    }
}