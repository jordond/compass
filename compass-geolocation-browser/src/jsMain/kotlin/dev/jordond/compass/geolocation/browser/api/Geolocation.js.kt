package dev.jordond.compass.geolocation.browser.api

import dev.jordond.compass.geolocation.browser.api.model.GeolocationPosition
import dev.jordond.compass.geolocation.browser.api.model.GeolocationPositionError
import dev.jordond.compass.geolocation.browser.internal.Object

public actual external class Geolocation {

    public actual fun getCurrentPosition(
        success: (GeolocationPosition?) -> Unit,
        error: (GeolocationPositionError) -> Unit,
        options: Object,
    )

    public actual fun watchPosition(
        success: (GeolocationPosition?) -> Unit,
        error: (GeolocationPositionError) -> Unit,
        options: Object,
    ): Int

    public actual fun clearWatch(watchId: Int)
}