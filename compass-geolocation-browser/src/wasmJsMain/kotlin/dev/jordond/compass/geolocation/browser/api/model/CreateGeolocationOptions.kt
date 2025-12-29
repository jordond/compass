package dev.jordond.compass.geolocation.browser.api.model

import dev.jordond.compass.geolocation.browser.internal.Object

@OptIn(ExperimentalWasmJsInterop::class)
public actual fun createGeolocationOptions(
    enableHighAccuracy: Boolean,
    timeout: Double,
    maximumAge: Double,
): Object =
    js("({enableHighAccuracy: enableHighAccuracy, timeout: timeout, maximumAge: maximumAge})")