package dev.jordond.compass.geolocation

import dev.drewhamilton.poko.Poko
import dev.jordond.compass.Priority

/**
 * Represents a request for location updates.
 *
 * @param priority The priority of the request.
 * @param interval The interval at which to receive location updates, in milliseconds.
 * @param maximumAge The maximum age of a cached location that can be used before a new location is
 * requested, in milliseconds.
 */
@Poko
public class LocationRequest(
    public val priority: Priority = Priority.Balanced,
    public val interval: Long = 5000L,
    public val maximumAge: Long = 0L,
)