package dev.jordond.compass.geolocation

import dev.drewhamilton.poko.Poko
import dev.jordond.compass.Location
import dev.jordond.compass.geolocation.exception.PermissionException

/**
 * Represents the result of a geolocation operation.
 */
public sealed interface GeolocatorResult {

    /**
     * Represents a unsuccessful geolocation operation.
     */
    public interface Error : GeolocatorResult {
        public val message: String
    }

    /**
     * Represents a successful geolocation operation.
     *
     * @property data The result of the geolocation operation.
     */
    @Poko
    public class Success(public val data: Location) : GeolocatorResult

    /**
     * Geocoding failed because the geocoder was unable to get a result for the input.
     */
    public object NotFound : Error {
        override val message: String = "No location found for the input."
    }

    /**
     * Geocoding operation failed at some point while geolocation.
     *
     * @param message A message describing the error that occurred.
     */
    @Poko
    public class GeolocationFailed(override val message: String) : Error

    /**
     * Geocoding operation failed because of a permission error.
     *
     * Either the user denied the permission, or the permission was denied forever.
     */
    @Poko
    @Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
    public class PermissionError(public val cause: PermissionException) : Error {
        override val message: String = cause.message ?: "Permission Error"
    }

    /**
     * Geocoding operation failed because the device does not support geolocation, or the device
     * does not support the specific geolocation operation.
     */
    public object NotSupported : Error {
        override val message: String = "Device does not support geolocation."
    }

    /**
     * Check if the result was unsuccessful.
     *
     * @return `true` if the result was unsuccessful, `false` otherwise.
     */
    public val isError: Boolean
        get() = this is Error

    /**
     * Get the result list data or `null` if the result was unsuccessful.
     *
     * @return The result data or `null` if the result was unsuccessful.
     */
    public fun getOrNull(): Location? = if (this is Success) data else null

    /**
     * Get the error or `null` if the result was successful.
     *
     * @return The error or `null` if the result was successful.
     */
    public fun errorOrNull(): Error? = if (this is Error) this else null

    /**
     * Perform an action if the result was successful.
     *
     * @param block The action to perform if the result was successful.
     * @return The original result.
     */
    public fun onSuccess(block: (Location) -> Unit): GeolocatorResult {
        if (this is Success) block(data)
        return this
    }

    /**
     * Perform an action if the result was unsuccessful.
     *
     * @param block The action to perform if the result was unsuccessful.
     * @return The original result.
     */
    public fun onFailed(block: (Error) -> Unit): GeolocatorResult {
        if (this is Error) block(this)
        return this
    }
}