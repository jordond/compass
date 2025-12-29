package dev.jordond.compass.geolocation

import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geolocation.exception.GeolocationException
import dev.jordond.compass.permissions.exception.PermissionException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Wrapper for platform specific geolocation services.
 */
public interface Locator {

    /**
     * A [Flow] of [Location] updates.
     *
     * This flow will only emit values when [track] is called. Call [stopTracking] to stop receiving
     * updates.
     *
     * If you call [stopTracking] this flow will stop emitting but it will never complete.
     *
     * @return A never completing [Flow] of [Location] updates.
     */
    public val locationUpdates: Flow<Location>


    public suspend fun lastLocation(priority: Priority = Priority.Balanced): Location?

    /**
     * Check if the platform supports geolocation.
     */
    public suspend fun isAvailable(): Boolean

    /**
     * Get the current location.
     *
     * @param priority The priority of the location request.
     * @return The current location, if available. If no location is available, this function
     * will throw a [NotFoundException].
     * @throws NotFoundException If no location is available.
     * @throws NotSupportedException If location services aren't available.
     * @throws GeolocationException If there is an error getting the location.
     * @throws PermissionException If the location permission isn't granted.
     */
    public suspend fun current(priority: Priority = Priority.Balanced): Location

    /**
     * Start tracking the location.
     *
     * Either collect the returned flow or use [locationUpdates] to receive location updates. If
     * you call [stopTracking], the flow will no longer emit values until [track] is called again.
     * But the flow won't be cancelled, so you should cancel it if you no longer need it.
     *
     * **Note:** The returned flow will not throw an exception, but this function will throw an
     * exception if location services aren't available or the location permission isn't granted.
     *
     * @param request The location request details.
     * @return A flow of location updates.
     * @throws NotSupportedException If location services aren't available.
     * @throws GeolocationException If there is an error getting the location.
     * @throws PermissionException If the location permission isn't granted.
     */
    public suspend fun track(request: LocationRequest = LocationRequest()): Flow<Location>

    /**
     * Stop tracking the location.
     *
     * [locationUpdates] will not longer emit values until [track] is called again. Note that the
     * flow won't be cancelled.
     */
    public fun stopTracking()

    public companion object
}

/**
 * Defines a [Locator] that requires a permission to check location.
 */
public interface PermissionLocator : Locator {

    public fun hasPermission(): Boolean
}

/**
 * A no-op [Locator] that is used when the platform does not support geolocation.
 *
 * This can be used as a fallback when the platform does not support geolocation.
 */
public object NotSupportedLocator : Locator {

    override val locationUpdates: Flow<Location> = emptyFlow()
    override suspend fun lastLocation(priority: Priority): Location = throw NotSupportedException()
    override suspend fun isAvailable(): Boolean = false
    override suspend fun current(priority: Priority): Location = throw NotSupportedException()
    override suspend fun track(request: LocationRequest): Flow<Location> = emptyFlow()
    override fun stopTracking() {}
}
