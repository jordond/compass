package dev.jordond.compass.geocoder.exception

/**
 * Indicates that geocoding is not supported on this platform.
 */
public class NotSupportedException : Throwable("Geocoding is not supported on this device")