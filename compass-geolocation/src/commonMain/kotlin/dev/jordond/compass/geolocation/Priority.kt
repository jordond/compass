package dev.jordond.compass.geolocation

/**
 * Represents the priority of a location request.
 */
public enum class Priority {

    /**
     * Balanced power and accuracy. The default priority.
     */
    Balanced,

    /**
     * High accuracy and high power consumption.
     *
     * **Note:** On Android this requires the LOCATION_FINE permission.
     */
    HighAccuracy,

    /**
     * Low power and low accuracy.
     */
    LowPower,

    /**
     * Lowest accuracy and lowest power consumption.
     */
    Passive,
}