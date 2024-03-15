package dev.jordond.compass

import dev.drewhamilton.poko.Poko

/**
 * A geographic location represented by latitude and longitude.
 *
 * Valid latitude values are between -90.0 and 90.0, both inclusive.
 * Valid longitude values are between -180.0 and 180.0, both inclusive.
 *
 * @property latitude The latitude of the location.
 * @property longitude The longitude of the location.
 */
@Poko
public class Location(
    public val latitude: Double,
    public val longitude: Double,
)