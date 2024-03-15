package dev.jordond.compass.geocoder.internal

public class GeocodeError(message: String?) : Throwable(
    "Geocoding failed: ${message ?: "Unknown error"}",
)