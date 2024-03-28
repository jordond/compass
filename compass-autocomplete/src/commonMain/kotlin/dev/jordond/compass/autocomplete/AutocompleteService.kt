package dev.jordond.compass.autocomplete

/**
 * Represents a service that provides autocomplete suggestions.
 *
 * @param T The type of the autocomplete suggestions.
 */
public interface AutocompleteService<T> {

    /**
     * Checks if the service is available.
     *
     * @return `true` if the service is available, `false` otherwise.
     */
    public fun isAvailable(): Boolean

    /**
     * Searches for autocomplete suggestions.
     *
     * @param query The search query.
     * @return A list of autocomplete suggestions.
     */
    public suspend fun search(query: String): List<T>

    public companion object
}