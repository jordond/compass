package dev.jordond.compass.geocoder

import dev.drewhamilton.poko.Poko

/**
 * Represents the result of a geocoding operation.
 *
 * @param T the type of data returned by the geocoding operation.
 */
public sealed interface GeocoderResult<out T> {

    /**
     * Represents a unsuccessful geocoding operation.
     */
    public interface Error : GeocoderResult<Nothing>

    /**
     * Represents a successful geocoding operation.
     *
     * @param T the type of data returned by the geocoding operation.
     * @property data A list of [T] returned by the geocoding operation.
     */
    @Poko
    public class Success<T>(public val data: List<T>) : GeocoderResult<T>

    /**
     * Geocoding failed because the geocoder was unable to get a result for the input.
     */
    public object NotFound : Error

    /**
     * Geocoding operation failed at some point while geocoding.
     *
     * @param message A message describing the error that occurred.
     */
    @Poko
    @Suppress("MemberVisibilityCanBePrivate")
    public class GeocodeFailed(public val message: String) : Error

    /**
     * Geocoding operation failed because the device does not support geocoding, or the device
     * does not support the specific geocoding operation.
     */
    public object NotSupported : Error

    /**
     * This error can occur if the latitude or longitude are out of range, or if the coordinates
     * are not valid coordinates.
     *
     * Valid coordinates are in the range of -90 to 90 for latitude and -180 to 180 for longitude.
     */
    public object InvalidCoordinates : Error

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
    public fun getOrNull(): List<T>? = if (this is Success<T>) data else null

    /**
     * Get the first result data or `null` if the result was unsuccessful.
     *
     * @return The first result data or `null` if the result was unsuccessful.
     */
    public fun getFirstOrNull(): T? = getOrNull()?.firstOrNull()

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
    public fun onSuccess(block: (List<T>) -> Unit): GeocoderResult<T> {
        if (this is Success<T>) block(data)
        return this
    }

    /**
     * Perform an action if the result was unsuccessful.
     *
     * @param block The action to perform if the result was unsuccessful.
     * @return The original result.
     */
    public fun onFailed(block: (Error) -> Unit): GeocoderResult<T> {
        if (this is Error) block(this)
        return this
    }
}