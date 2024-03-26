package dev.jordond.compass

import dev.drewhamilton.poko.Poko

/**
 * Object representing a user's location.
 *
 * @property coordinates The coordinates of the location.
 * @property accuracy The accuracy of the location in meters, these values can be empty depending on
 * the accuracy of the location request.
 * @property azimuth The azimuth of the location in degrees, this value can be empty depending on
 * the accuracy of the location request.
 * @property speed The speed of the location in meters per second, this value can be empty depending
 * on the accuracy of the location request.
 * @property altitude The altitude of the location in meters, this value can be empty depending on
 * the accuracy of the location request.
 * @property timestampMillis The timestamp of the location in milliseconds since epoch.
 */
@Poko
public class Location(
    public val coordinates: Coordinates,
    public val accuracy: Double,
    public val azimuth: Azimuth?,
    public val speed: Speed?,
    public val altitude: Altitude?,
    public val timestampMillis: Long,
)

/**
 * Object representing the users azimuth.
 *
 * **Note:** These values can be empty depending on the accuracy of the location request.
 *
 * @property degrees The azimuth in degrees.
 * @property accuracy The accuracy of the azimuth in degrees.
 */
@Poko
public class Azimuth(
    public val degrees: Float,
    public val accuracy: Float?,
)

/**
 * Object representing the speed of the user.
 *
 * **Note:** These values can be empty depending on the accuracy of the location request.
 *
 * @property mps The speed in meters per second.
 * @property accuracy The accuracy of the speed in meters per second.
 */
@Poko
public class Speed(
    public val mps: Float,
    public val accuracy: Float?,
)

/**
 * Object representing the altitude of the user.
 *
 * **Note:** These values can be empty depending on the accuracy of the location request.
 *
 * @property meters The altitude in meters.
 * @property accuracy The accuracy of the altitude in meters.
 */
@Poko
public class Altitude(
    public val meters: Double,
    public val accuracy: Float?,
)