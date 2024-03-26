package dev.jordond.compass.geolocation

import dev.drewhamilton.poko.Poko
import dev.jordond.compass.Location

/**
 * Represents the current status of a [Geolocator] tracking operation.
 */
public sealed interface TrackingStatus {

    /**
     * Check whether the tracking status is active.
     *
     * @returns `true` if the tracking status is [Tracking] or [Update], `false` otherwise.
     */
    public val isActive: Boolean
        get() = this is Tracking || this is Update

    /**
     * Tracker is idle and waiting to start.
     */
    public data object Idle : TrackingStatus

    /**
     * Tracker is actively tracking the user's location, and waiting for an update.
     */
    public data object Tracking : TrackingStatus

    /**
     * Tracker has received an update to the user's location.
     *
     * @property location The updated location.
     */
    @Poko
    public class Update(public val location: Location) : TrackingStatus

    /**
     * Tracker has encountered an error and is no longer active.
     *
     * @property cause The error that caused the tracking to stop.
     */
    @Poko
    public class Error(public val cause: GeolocatorResult.Error) : TrackingStatus
}