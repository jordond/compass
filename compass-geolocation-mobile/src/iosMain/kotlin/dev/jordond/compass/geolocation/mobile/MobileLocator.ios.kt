package dev.jordond.compass.geolocation.mobile

import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.mobile.internal.LocationManagerDelegate
import dev.jordond.compass.geolocation.mobile.internal.cachedLocationOrNull
import dev.jordond.compass.geolocation.mobile.internal.currentTimeMillis
import dev.jordond.compass.geolocation.mobile.internal.toIosPriority
import dev.jordond.compass.geolocation.mobile.internal.toModel
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import dev.jordond.compass.permissions.throwOnError
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import platform.CoreLocation.CLLocationManager

internal actual fun createLocator(
    permissionController: LocationPermissionController,
): MobileLocator {
    return IosLocator(permissionController)
}

internal class IosLocator(
    private val permissionController: LocationPermissionController,
    private val locationDelegate: LocationManagerDelegate = LocationManagerDelegate(),
) : MobileLocator {

    private val _locationUpdates = MutableSharedFlow<Location>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val locationUpdates: Flow<Location> = _locationUpdates

    // CoreLocation has no notion of an update interval, it reports a fix whenever it has one. These
    // throttle what reaches [locationUpdates] so that `LocationRequest.interval` means the same
    // thing here as it does on Android. Written from [track] before it hands off to the main
    // thread, and read from there, so the dispatch establishes the ordering between them.
    private var updateIntervalMillis: Long = 0L
    private var lastEmittedAtMillis: Long = 0L

    init {
        locationDelegate.monitorLocation { location ->
            if (shouldEmit()) {
                _locationUpdates.tryEmit(location.toModel())
            }
        }
    }

    override suspend fun lastLocation(priority: Priority): Location? {
        requirePermission(priority)
        return locationDelegate.lastLocation()?.toModel()
    }

    override suspend fun isAvailable(): Boolean {
        return CLLocationManager.locationServicesEnabled()
    }

    override fun hasPermission(): Boolean {
        return permissionController.hasPermission()
    }

    override suspend fun current(priority: Priority): Location {
        requirePermission(priority)
        return locationDelegate.requestLocation(priority.toIosPriority).toModel()
    }

    override suspend fun current(request: LocationRequest): Location =
        cachedLocationOrNull(request) ?: current(request.priority)

    override suspend fun track(request: LocationRequest): Flow<Location> {
        requirePermission(request.priority)
        if (locationDelegate.isTracking) return locationUpdates

        updateIntervalMillis = request.interval
        lastEmittedAtMillis = 0L
        locationDelegate.trackLocation(request.priority.toIosPriority)

        return locationUpdates
    }

    /**
     * Whether enough time has passed since the last update to forward another one.
     *
     * Only called from the main thread, where CoreLocation delivers its callbacks, so the
     * bookkeeping needs no synchronizing. The first update after [track] always goes through.
     */
    private fun shouldEmit(): Boolean {
        if (updateIntervalMillis <= 0L) return true

        val now = currentTimeMillis()
        if (now - lastEmittedAtMillis < updateIntervalMillis) return false

        lastEmittedAtMillis = now
        return true
    }

    override fun stopTracking() {
        locationDelegate.stopTracking()
    }

    private suspend fun requirePermission(priority: Priority) {
        val state = permissionController.requirePermissionFor(priority)
        if (state != PermissionState.Granted) {
            state.throwOnError()
        }
    }
}
