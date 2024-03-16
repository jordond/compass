package dev.jordond.compass.geocoder.api

import dev.jordond.compass.Location
import dev.jordond.compass.Place

/**
 * A wrapper around an HTTP based geocoding API.
 */
public interface HttpGeocoderApi {

    /**
     * Returns true if the geocoder is available on the current service.
     */
    public fun isAvailable(): Boolean = true

    /**
     * Get a list of [Place]s from latitude and longitude coordinates.
     *
     * @param location The coordinates to reverse geocode.
     * @return The address of the coordinates or empty list if the address could not be found.
     */
    public suspend fun reverse(location: Location): List<Place>

    /**
     * Get a list of [Place]s from an address.
     *
     * @param address The address to geocode.
     * @return A list of [Place]s that match the address, or an empty list if no matches were found.
     */
    public suspend fun reverse(address: String): List<Place>

    /**
     * Get a list of [Location]s from an address.
     *
     * @param address The address to geocode.
     * @return A list of [Location]s that match the address, or an empty list if
     * no matches were found.
     */
    public suspend fun forward(address: String): List<Location>
}