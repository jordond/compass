package dev.jordond.compass.geolocation

import dev.jordond.compass.Location
import kotlinx.coroutines.flow.Flow

public interface Locator {

    public fun isAvailable(): Boolean

    public suspend fun last(): Location?

    public suspend fun current(priority: Priority = Priority.Balanced): Location

    public fun track(request: LocationRequest = LocationRequest()): Flow<Location>

    public fun stopTracking()

    public companion object
}