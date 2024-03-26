package dev.jordond.compass.geocoder.exception

/**
 * Indicate something went wrong while using the geocoding service.
 *
 * @param message The error message.
 */
public class GeocodeException(message: String?) : Throwable(
    "Geocoding failed: ${message ?: "Unknown error"}",
)