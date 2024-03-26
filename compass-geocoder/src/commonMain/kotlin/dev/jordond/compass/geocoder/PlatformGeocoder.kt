package dev.jordond.compass.geocoder

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place

/**
 * Wrapper for platform-specific geocoding services.
 */
public interface PlatformGeocoder {

    /**
     * Returns true if the geocoder is available on the current platform.
     *
     * @return `true` if the geocoder is available, false otherwise.
     */
    public fun isAvailable(): Boolean

    /**
     * Get a list of [Coordinates]s from an address.
     */
    public suspend fun forward(address: String): List<Coordinates>

    /**
     *  Get a list of [Place]s from latitude and longitude coordinates.
     *
     * @param latitude The latitude of the coordinates.
     * @param longitude The longitude of the coordinates.
     * @return The address of the coordinates or empty list if the address could not be found.
     */
    public suspend fun reverse(latitude: Double, longitude: Double): List<Place>

    public companion object
}

/**
 * A no-op [PlatformGeocoder] that is used when the platform does not support geocoding.
 *
 * This can be used as a fallback when the platform does not support geocoding.
 */
public object NotSupportedPlatformGeocoder : PlatformGeocoder {

    override fun isAvailable(): Boolean = false
    override suspend fun forward(address: String): List<Coordinates> = emptyList()
    override suspend fun reverse(latitude: Double, longitude: Double): List<Place> = emptyList()
}