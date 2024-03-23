package dev.jordond.compass.geolocation

import dev.jordond.compass.Location
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geolocation.exception.GeolocationException
import dev.jordond.compass.geolocation.exception.PermissionException
import dev.jordond.compass.geolocation.internal.DefaultGeolocator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

/**
 * Provides geolocation operations:
 *
 * - Track the location.
 * - Get the current location.
 */
public interface Geolocator {

    public val locator: Locator

    /**
     * A flow of location updates.
     *
     * If [track] is called, this flow will emit the new locations as they are received. Otherwise,
     * this flow won't emit any values.
     *
     * You can stop receiving updates by calling [stopTracking]. But this flow won't be cancelled.
     */
    public val locationUpdates: Flow<Location>

    /**
     * Check if location services are available.
     *
     * @return `true` if location services are available, `false` otherwise.
     */
    public fun isAvailable(): Boolean

    /**
     * Get the current location.
     *
     * @param priority The priority of the location request.
     * @return A [GeolocatorResult] with the success or error state.
     */
    public suspend fun current(priority: Priority = Priority.Balanced): GeolocatorResult

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
}

/**
 * Create a new [Geolocator] using the provided [locator] and [dispatcher].
 *
 * @param locator The locator to use for geolocation.
 * @param dispatcher The dispatcher to use for the geolocator.
 * @return A new geolocator instance
 */
public fun Geolocator(
    locator: Locator,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = DefaultGeolocator(locator, dispatcher)