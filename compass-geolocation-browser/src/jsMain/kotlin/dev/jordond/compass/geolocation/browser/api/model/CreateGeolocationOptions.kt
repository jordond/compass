package dev.jordond.compass.geolocation.browser.api.model

import dev.jordond.compass.geolocation.browser.internal.Object

public actual fun createGeolocationOptions(
    enableHighAccuracy: Boolean,
    timeout: Double,
    maximumAge: Double,
): Object = js(
    "({enableHighAccuracy: enableHighAccuracy, timeout: timeout, maximumAge: maximumAge})",
) as Object