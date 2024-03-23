package dev.jordond.compass.geolocation.browser.internal

import dev.jordond.compass.Location
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.Priority
import dev.jordond.compass.geolocation.browser.BrowserLocator
import kotlinx.coroutines.flow.Flow

internal class DefaultBrowserLocator : BrowserLocator{

    override val locationUpdates: Flow<Location>
        get() = TODO("Not yet implemented")

    override fun isAvailable(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun current(priority: Priority): Location {
        TODO("Not yet implemented")
    }

    override suspend fun track(request: LocationRequest): Flow<Location> {
        TODO("Not yet implemented")
    }

    override fun stopTracking() {
        TODO("Not yet implemented")
    }
}