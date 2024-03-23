package dev.jordond.compass.geolocation

import dev.drewhamilton.poko.Poko

/**
 * Represents a request for location updates.
 *
 * @param priority The priority of the request.
 * @param interval The interval at which to receive location updates, in milliseconds.
 */
@Poko
public class LocationRequest(
    public val priority: Priority = Priority.Balanced,
    public val interval: Long = 5000L,
)