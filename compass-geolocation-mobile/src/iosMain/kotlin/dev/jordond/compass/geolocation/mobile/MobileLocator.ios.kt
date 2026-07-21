package dev.jordond.compass.geolocation.mobile

import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.mobile.internal.LocationManagerDelegate
import dev.jordond.compass.geolocation.mobile.internal.cachedLocationOrNull
import dev.jordond.compass.geolocation.mobile.internal.monotonicMillis
import dev.jordond.compass.geolocation.mobile.internal.toIosPriority
import dev.jordond.compass.geolocation.mobile.internal.toModel
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import dev.jordond.compass.permissions.throwOnError
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import platform.CoreLocation.CLLocationManager

/**
 * Marks that no update has been forwarded yet, so the next one goes through whatever the interval.
 */
private const val NOT_EMITTED_YET = -1L

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
    // thing here as it does on Android. Both are written by [track] on whichever thread the caller
    // is on and read by [shouldEmit] on the main thread, so neither can be a plain `var`.
    private val updateIntervalMillis = atomic(0L)
    private val lastEmittedAtMillis = atomic(NOT_EMITTED_YET)

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

        // Applied before the already-tracking check so that tracking again with a different
        // interval takes effect, rather than silently keeping whatever the first call asked for.
        updateIntervalMillis.value = request.interval
        if (locationDelegate.isTracking) return locationUpdates

        lastEmittedAtMillis.value = NOT_EMITTED_YET
        locationDelegate.trackLocation(request.priority.toIosPriority)

        return locationUpdates
    }

    /**
     * Whether enough time has passed since the last update to forward another one.
     *
     * Only called from the main thread, where CoreLocation delivers its callbacks. The first
     * update after [track] always goes through.
     */
    private fun shouldEmit(): Boolean {
        val interval = updateIntervalMillis.value
        if (interval <= 0L) return true

        val now = monotonicMillis()
        val last = lastEmittedAtMillis.value
        if (last != NOT_EMITTED_YET && now - last < interval) return false

        lastEmittedAtMillis.value = now
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
