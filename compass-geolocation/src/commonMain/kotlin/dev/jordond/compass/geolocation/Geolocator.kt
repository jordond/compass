package dev.jordond.compass.geolocation

import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.internal.DefaultGeolocator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Provides geolocation operations:
 *
 * - Track the location.
 * - Get the current location.
 */
public interface Geolocator {

    /**
     * A flow of the current status of location tracking.
     *
     * @see TrackingStatus
     *
     * If [track] is called, this flow will emit the new locations as they are received. Otherwise,
     * this flow won't emit any values.
     *
     * You can stop receiving updates by calling [stopTracking]. But this flow won't be cancelled.
     */
    public val trackingStatus: Flow<TrackingStatus>

    /**
     * A flow of location updates.
     *
     * If [track] is called, this flow will emit the new locations as they are received. Otherwise,
     * this flow won't emit any values.
     *
     * You can stop receiving updates by calling [stopTracking]. But this flow won't be cancelled.
     */
    public val locationUpdates: Flow<Location>
        get() = trackingStatus.filterIsInstance<TrackingStatus.Update>().map { it.location }

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
     * @param request The location request details.
     * @return A flow of location tracking updates.
     */
    public fun track(request: LocationRequest = LocationRequest()): Flow<TrackingStatus>

    /**
     * Start tracking the location and wait for the first location update.
     *
     * Useful for checking if the tracking has started or if the permission has been denied.
     *
     * @param request The location request details.
     * @return The first location update.
     */
    public suspend fun startTracking(request: LocationRequest = LocationRequest()): TrackingStatus =
        track(request).first()

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