package dev.jordond.compass.geolocation.browser.api.model

/**
 * The GeolocationCoordinates interface represents the position and altitude of the device on Earth,
 * as well as the accuracy with which these properties are calculated. The geographic position
 * information is provided in terms of World Geodetic System coordinates (WGS84).
 *
 * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/GeolocationCoordinates).
 *
 * @property latitude Returns a double representing the position's latitude in decimal degrees.
 * @property longitude Returns a double representing the position's longitude in decimal degrees.
 * @property accuracy Returns a double representing the accuracy of the latitude and longitude
 * properties, expressed in meters.
 * @property altitude Returns a double representing the position's altitude in meters, relative to
 * nominal sea level. This value can be null if the implementation cannot provide the data.
 * @property altitudeAccuracy Returns a double representing the accuracy of the altitude expressed
 * in meters. This value can be null if the implementation cannot provide the data.
 * @property heading Returns a double representing the direction towards which the device is facing.
 * This value, specified in degrees, indicates how far off from heading true north the device is.
 * 0 degrees represents true north, and the direction is determined clockwise (which means that east
 * is 90 degrees and west is 270 degrees). If speed is 0, heading is NaN. If the device is unable to
 * provide heading information, this value is null.
 * @property speed Returns a double representing the velocity of the device in meters per second.
 * This value can be null.
 */
public external class GeolocationCoordinates {

    public val latitude: Double
    public val longitude: Double
    public val accuracy: Double
    public val altitude: Double?
    public val altitudeAccuracy: Double?
    public val heading: Double?
    public val speed: Double?
}
