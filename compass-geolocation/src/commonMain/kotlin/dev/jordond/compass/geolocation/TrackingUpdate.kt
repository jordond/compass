package dev.jordond.compass.geolocation

import dev.drewhamilton.poko.Poko
import dev.jordond.compass.Location

public sealed interface TrackingStatus {

    public val isActive: Boolean
        get() = this is Tracking || this is Update

    public data object Idle : TrackingStatus

    public data object Tracking : TrackingStatus

    @Poko
    public class Update(public val location: Location) : TrackingStatus

    @Poko
    public class Error(public val cause: GeolocatorResult.Error) : TrackingStatus
}