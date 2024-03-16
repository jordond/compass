package dev.jordond.compass.geocoder

import dev.jordond.compass.Location
import dev.jordond.compass.Place

/**
 * Wrapper for platform-specific geocoding services.
 */
public interface PlatformGeocoder {

    /**
     * Returns true if the geocoder is available on the current platform.
     *
     * On iOS this always returns `true.
     *
     * @return `true` if the geocoder is available, false otherwise.
     */
    public fun isAvailable(): Boolean

    /**
     *  Get a list of [Place]s from latitude and longitude coordinates.
     *
     * @param location The coordinates to reverse geocode.
     * @return The address of the coordinates or empty list if the address could not be found.
     */
    public suspend fun placeFromLocation(location: Location): List<Place>

    /**
     * Get a list of [Place]s from an address.
     *
     * @param address The address to geocode.
     * @return A list of [Place]s that match the address, or an empty list if no matches were found.
     */
    public suspend fun placeFromAddress(address: String): List<Place>

    /**
     * Get a list of [Location]s from an address.
     */
    public suspend fun locationFromAddress(address: String): List<Location>

    public companion object
}