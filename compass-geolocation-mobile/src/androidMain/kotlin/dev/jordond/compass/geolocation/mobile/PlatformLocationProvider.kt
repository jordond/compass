package dev.jordond.compass.geolocation.mobile

import android.location.Location
import dev.jordond.compass.InternalCompassApi
import dev.jordond.compass.Priority
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.geolocation.LocationRequest
import kotlinx.coroutines.flow.Flow

/**
 * The Android location source backing a [MobileLocator].
 *
 * Every location returning member assumes the caller has already checked the location permission;
 * [MobileLocator] does that before it delegates here.
 */
@InternalCompassApi
public interface PlatformLocationProvider {

    /**
     * Locations emitted while [startTracking] is active.
     */
    public val locationUpdates: Flow<Location>

    /**
     * The most recent location the source already holds, or `null` when it holds none.
     */
    public suspend fun lastLocation(): Location?

    /**
     * Whether location services are usable right now.
     */
    public suspend fun locationEnabled(): Boolean

    /**
     * Resolve a fresh location.
     *
     * @throws NotFoundException If no location could be resolved.
     */
    public suspend fun currentLocation(priority: Priority): Location

    /**
     * Begin emitting to [locationUpdates]. A no-op when tracking is already active.
     *
     * @throws NotFoundException If no source can serve [request].
     */
    public fun startTracking(request: LocationRequest)

    /**
     * Stop emitting to [locationUpdates]. A no-op when tracking is not active.
     */
    public fun stopTracking()
}
