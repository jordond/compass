package dev.jordond.compass.geolocation

import dev.jordond.compass.ExtendedLocation
import dev.jordond.compass.Location
import kotlinx.coroutines.flow.Flow

public interface Locator {

    public fun isAvailable(): Boolean

    public suspend fun last(): Location

    public suspend fun current(): Location

    public suspend fun extended(): ExtendedLocation

    public fun track(): Flow<Location>

    public fun trackExtended(): Flow<ExtendedLocation>

    public fun stopTracking()

    public companion object
}