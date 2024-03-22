package dev.jordond.compass.geolocation

import dev.jordond.compass.Location
import dev.jordond.compass.exception.NotSupportedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

public interface Locator {

    public fun isAvailable(): Boolean

    public suspend fun last(): Location?

    public suspend fun current(priority: Priority = Priority.Balanced): Location

    public fun track(request: LocationRequest = LocationRequest()): Flow<Location>

    public fun stopTracking()

    public companion object
}

/**
 * A no-op [Locator] that is used when the platform does not support geolocation.
 *
 * This can be used as a fallback when the platform does not support geolocation.
 */
public object NotSupportedLocator : Locator {

    override fun isAvailable(): Boolean = false
    override suspend fun last(): Location? = null
    override suspend fun current(priority: Priority): Location = throw NotSupportedException()
    override fun track(request: LocationRequest): Flow<Location> = emptyFlow()
    override fun stopTracking() {}
}