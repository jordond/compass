package dev.jordond.compass.geolocation.browser.api

import dev.jordond.compass.InternalCompassApi

/**
 * Browser provided Navigator object for accessing Geolocation API.
 *
 * See [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/Navigator)
 */
@InternalCompassApi
public expect val navigator: Navigator?

/**
 * Browser provided Navigator object for accessing Geolocation API.
 *
 * See [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/Navigator)
 */
@InternalCompassApi
public expect class Navigator {

    /**
     * GeoLocation API for accessing the browser's location data.
     *
     * See [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation)
     */
    public val geolocation: Geolocation?
}