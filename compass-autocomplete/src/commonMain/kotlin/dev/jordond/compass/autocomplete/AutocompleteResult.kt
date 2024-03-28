package dev.jordond.compass.autocomplete

import dev.drewhamilton.poko.Poko

/**
 * Represents the result of a autocomplete operation.
 */
public sealed interface AutocompleteResult<out T> {

    /**
     * Represents a unsuccessful autocomplete operation.
     */
    public interface Error : AutocompleteResult<Nothing> {
        public val message: String
    }

    /**
     * Represents a successful autocomplete operation.
     *
     * @property data The result of the autocomplete operation.
     */
    @Poko
    public class Success<T>(public val data: List<T>) : AutocompleteResult<T>

    /**
     * Geocoding operation failed at some point while autocomplete.
     *
     * @param message A message describing the error that occurred.
     */
    @Poko
    public class Failed(override val message: String) : Error

    /**
     * Geocoding operation failed because the device does not support autocomplete, or the device
     * does not support the specific autocomplete operation.
     */
    public object NotSupported : Error {
        override val message: String = "Device does not support autocomplete."
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
    public fun getOrNull(): List<T>? = if (this is Success) data else null

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
    public fun onSuccess(block: (List<T>) -> Unit): AutocompleteResult<T> {
        if (this is Success) block(data)
        return this
    }

    /**
     * Perform an action if the result was unsuccessful.
     *
     * @param block The action to perform if the result was unsuccessful.
     * @return The original result.
     */
    public fun onFailed(block: (Error) -> Unit): AutocompleteResult<T> {
        if (this is Error) block(this)
        return this
    }
}