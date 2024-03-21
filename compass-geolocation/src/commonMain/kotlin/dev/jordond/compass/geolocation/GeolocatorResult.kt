package dev.jordond.compass.geolocation

import dev.drewhamilton.poko.Poko

/**
 * Represents the result of a geolocation operation.
 *
 * @param T the type of data returned by the geolocation operation.
 */
public sealed interface GeolocatorResult<out T> {

    /**
     * Represents a unsuccessful geolocation operation.
     */
    public interface Error : GeolocatorResult<Nothing>

    /**
     * Represents a successful geolocation operation.
     *
     * @param T the type of data returned by the geolocation operation.
     * @property data The result of the geolocation operation.
     */
    @Poko
    public class Success<T>(public val data: T) : GeolocatorResult<T>

    /**
     * Geocoding failed because the geocoder was unable to get a result for the input.
     */
    public object NotFound : Error

    /**
     * Geocoding operation failed at some point while geolocation.
     *
     * @param message A message describing the error that occurred.
     */
    @Poko
    @Suppress("MemberVisibilityCanBePrivate")
    public class GeolocationFailed(public val message: String) : Error

    /**
     * Geocoding operation failed because the device does not support geolocation, or the device
     * does not support the specific geolocation operation.
     */
    public object NotSupported : Error

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
    public fun getOrNull(): T? = if (this is Success<T>) data else null

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
    public fun onSuccess(block: (T) -> Unit): GeolocatorResult<T> {
        if (this is Success<T>) block(data)
        return this
    }

    /**
     * Perform an action if the result was unsuccessful.
     *
     * @param block The action to perform if the result was unsuccessful.
     * @return The original result.
     */
    public fun onFailed(block: (Error) -> Unit): GeolocatorResult<T> {
        if (this is Error) block(this)
        return this
    }
}