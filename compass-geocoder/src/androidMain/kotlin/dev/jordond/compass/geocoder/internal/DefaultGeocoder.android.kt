package dev.jordond.compass.geocoder.internal

import android.location.Geocoder
import android.os.Build
import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.internal.context.ContextProvider

private const val MAX_RESULTS = 5

/**
 * Check if the device supports geocoding.
 *
 * @return `true` if the device supports geocoding, `false` otherwise.
 */
internal actual fun geocoderAvailable(): Boolean = Geocoder.isPresent()