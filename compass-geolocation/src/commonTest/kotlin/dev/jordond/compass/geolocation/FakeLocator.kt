package dev.jordond.compass.geolocation

import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.internal.DefaultGeolocator
import dev.jordond.compass.permissions.LocationAccuracy
import dev.jordond.compass.test.location
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope

/**
 * A geolocator over [locator] that runs on the scheduler of the calling test, and whose collectors
 * end with the test.
 */
internal fun TestScope.geolocator(locator: Locator = FakeLocator()): DefaultGeolocator =
    DefaultGeolocator(
        locator = locator,
        dispatcher = StandardTestDispatcher(testScheduler),
        coroutineScope = backgroundScope,
    )

internal open class FakeLocator : Locator {
    private val updates = MutableSharedFlow<Location>()
    override val locationUpdates: Flow<Location> = updates

    var available = true
    var onCurrent: suspend () -> Location = { location() }
    var onLastLocation: suspend () -> Location? = { location() }

    /**
     * Never completes by default, the way a platform locator behaves, and counts in
     * [trackCancellations] when its collector is cancelled.
     */
    var onTrack: suspend () -> Flow<Location> = {
        flow {
            try {
                awaitCancellation()
            } finally {
                trackCancellations++
            }
        }
    }

    var availableCalls = 0
    var currentCalls = 0
    var trackCalls = 0
    var trackCancellations = 0
    var stopCalls = 0
    val currentRequests = mutableListOf<LocationRequest>()
    val lastLocationRequests = mutableListOf<LocationRequest>()

    /**
     * Sends [location] through [locationUpdates] once something is collecting it. The flow keeps no
     * replay, so emitting before the geolocator has subscribed would silently drop the value.
     */
    suspend fun emit(location: Location) {
        updates.subscriptionCount.first { count -> count > 0 }
        updates.emit(location)
    }

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
