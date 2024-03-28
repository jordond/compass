package dev.jordond.compass.geolocation.browser.api

import dev.jordond.compass.InternalCompassApi
import dev.jordond.compass.geolocation.browser.api.model.GeolocationPosition
import dev.jordond.compass.geolocation.browser.api.model.GeolocationPositionError
import dev.jordond.compass.geolocation.browser.internal.Object

/**
 * GeoLocation API for accessing the browser's location data.
 *
 * See [Geolocation](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation)
 */
@InternalCompassApi
public expect class Geolocation {

    /**
     * Returns the current position of the device.
     *
     * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation/getCurrentPosition).
     *
     * @param success Callback function that is called with the current position.
     * @param error Callback function that is called when an error occurs.
     * @param options An optional object that provides options for the request.
     */
    public fun getCurrentPosition(
        success: (GeolocationPosition?) -> Unit,
        error: (GeolocationPositionError) -> Unit,
        options: Object,
    )

    /**
     * Starts watching the position of the device.
     *
     * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation/watchPosition).
     *
     * @param success Callback function that is called with the current position.
     * @param error Callback function that is called when an error occurs.
     * @param options An optional object that provides options for the request.
     * @return A watch ID that can be used to clear the watch.
     */
    public fun watchPosition(
        success: (GeolocationPosition?) -> Unit,
        error: (GeolocationPositionError) -> Unit,
        options: Object,
    ): Int

    /**
     * Clear an existing watch operation.
     *
     * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation/clearWatch).
     *
     * @param watchId The ID of the watch operation to clear.
     */
    public fun clearWatch(watchId: Int)
}