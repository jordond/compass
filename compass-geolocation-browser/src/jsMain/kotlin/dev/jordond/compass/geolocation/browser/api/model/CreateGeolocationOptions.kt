package dev.jordond.compass.geolocation.browser.api.model

import kotlin.js.json

public actual fun createGeolocationOptions(
    enableHighAccuracy: Boolean,
    timeout: Double,
    maximumAge: Double,
): dynamic = json(
    "enableHighAccuracy" to enableHighAccuracy,
    "timeout" to timeout,
    "maximumAge" to maximumAge,
)