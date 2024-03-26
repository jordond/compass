package dev.jordond.compass.exception

/**
 * Thrown when a geocoder or geolocation operation returns null.
 */
public class NotFoundException : Throwable("Unable to find requested result.")