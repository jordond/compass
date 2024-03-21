package dev.jordond.compass

import dev.drewhamilton.poko.Poko

@Poko
public class Location(
    public val coordinates: Coordinates,
    public val accuracy: Meters,
    public val azimuth: Azimuth,
    public val speed: Speed,
    public val altitude: Altitude,
    public val timestampMillis: Long,
)

@Poko
public class Azimuth(
    public val degrees: Float,
    public val accuracy: Float?,
)

@Poko
public class Speed(
    public val mps: Float,
    public val accuracy: Float?,
)

@Poko
public class Altitude(
    public val meters: Meters,
    public val accuracy: Float?,
)