package dev.jordond.compass.geolocation.browser.api.model

import dev.jordond.compass.geolocation.browser.api.Geolocation
import dev.jordond.compass.geolocation.browser.internal.Object

/**
 * Represents the options that can be passed to the [Geolocation] methods.
 *
 * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation/getCurrentPosition#options)
 *
 * @param enableHighAccuracy A boolean value that indicates the application would like to receive
 * the best possible results. If true and if the device is able to provide a more accurate position,
 * it will do so. Note that this can result in slower response times or increased power consumption
 * (with a GPS chip on a mobile device for example). On the other hand, if false, the device can
 * take the liberty to save resources by responding more quickly and/or using less power.
 * @param timeout A positive long value representing the maximum length of time (in milliseconds)
 * the device is allowed to take in order to return a position. The default value is Infinity,
 * meaning that getCurrentPosition() won't return until the position is available.
 * @param maximumAge A positive long value indicating the maximum age in milliseconds of a possible cached
 * position that is acceptable to return. If set to 0, it means that the device cannot use a cached
 * position and must attempt to retrieve the real current position. If set to Infinity the device
 * must return a cached position regardless of its age. Default: 0.
 */
public expect fun createGeolocationOptions(
    enableHighAccuracy: Boolean,
    timeout: Double,
    maximumAge: Double,
): Object