package dev.jordond.compass.geocoder

import dev.jordond.compass.Location
import dev.jordond.compass.Place

/**
 * A no-op [PlatformGeocoder] that is used when the platform does not support geocoding.
 *
 * This can be used as a fallback when the platform does not support geocoding.
 */
public object NotSupportedPlatformGeocoder : PlatformGeocoder {

    override fun isAvailable(): Boolean = false
    override suspend fun forward(address: String): List<Location> = emptyList()
    override suspend fun reverse(latitude: Double, longitude: Double): List<Place> = emptyList()
}