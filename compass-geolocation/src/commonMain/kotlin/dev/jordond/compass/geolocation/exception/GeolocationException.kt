package dev.jordond.compass.geolocation.exception

/**
 * Indicate something went wrong while using the geolocation service.
 *
 * @param message The error message.
 */
public class GeolocationException(message: String?) : Throwable(
    "Geolocation failed: ${message ?: "Unknown error"}",
)