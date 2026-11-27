package dev.jordond.compass.geolocation

import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.permissions.LocationAccuracy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow

internal open class FakeLocator : Locator {
    val updates = MutableSharedFlow<Location>()
    override val locationUpdates: Flow<Location> = updates

    var available = true
    var onCurrent: suspend () -> Location = { location() }
    var onLastLocation: suspend () -> Location? = { location() }
    var onTrack: suspend () -> Flow<Location> = { emptyFlow() }

    var availableCalls = 0
    var currentCalls = 0
    var trackCalls = 0
    var stopCalls = 0
    val currentRequests = mutableListOf<LocationRequest>()
    val lastLocationRequests = mutableListOf<LocationRequest>()

    override suspend fun lastLocation(priority: Priority): Location? =
        lastLocation(LocationRequest(priority))

    override suspend fun lastLocation(request: LocationRequest): Location? {
        lastLocationRequests += request
        return onLastLocation()
    }

    override suspend fun isAvailable(): Boolean {
        availableCalls++
        return available
    }

    override suspend fun current(priority: Priority): Location = current(LocationRequest(priority))

    override suspend fun current(request: LocationRequest): Location {
        currentCalls++
        currentRequests += request
        return onCurrent()
    }

    override suspend fun track(request: LocationRequest): Flow<Location> {
        trackCalls++
        return onTrack()
    }

    override fun stopTracking() {
        stopCalls++
    }
}

internal class FakePermissionLocator(
    private val granted: Boolean,
    private val accuracy: LocationAccuracy,
) : FakeLocator(), PermissionLocator {
    override fun hasPermission(): Boolean = granted
    override fun grantedAccuracy(): LocationAccuracy = accuracy
}
