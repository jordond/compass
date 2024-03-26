package dev.jordond.compass.geolocation.browser.api.model

/**
 * The GeolocationPosition interface represents the position of the concerned device at a given
 * time. The position, represented by a GeolocationCoordinates object, comprehends the 2D position
 * of the device, on a spheroid representing the Earth, but also its altitude and its speed.
 *
 * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/GeolocationPosition)
 *
 * @property coords The [GeolocationCoordinates] object containing the device's current location.
 * @property timestamp The time at which the location was retrieved.
 */
public external class GeolocationPosition {

    public val coords: GeolocationCoordinates

    public val timestamp: Double
}