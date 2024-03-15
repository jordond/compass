package dev.jordond.compass.internal

public class GeocodeError(message: String?) : Throwable(
    "Geocoding failed: ${message ?: "Unknown error"}",
)