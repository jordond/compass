package dev.jordond.compass.autocomplete

public interface AutocompleteService<T> {

    public fun isAvailable(): Boolean

    public suspend fun search(query: String): List<T>

    public companion object
}