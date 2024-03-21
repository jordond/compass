package dev.jordond.compass.geolocation

import dev.drewhamilton.poko.Poko

@Poko
public class LocationRequest(
    public val priority: Priority = Priority.Balanced,
    public val interval: Long = 1000L,
    public val granularity: Granularity = Granularity.PermissionsLevel,
)