package dev.jordond.compass.geocoder.internal

import android.location.Geocoder

internal const val MAX_RESULTS = 5

/**
 * Check if the device supports geocoding.
 *
 * @return `true` if the device supports geocoding, `false` otherwise.
 */
internal actual fun geocoderAvailable(): Boolean = Geocoder.isPresent()