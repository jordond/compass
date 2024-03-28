package dev.jordond.compass.autocomplete

import dev.jordond.compass.autocomplete.internal.DefaultAutocomplete
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public interface Autocomplete<T> {

    public val options: AutocompleteOptions

    public suspend fun search(query: String): AutocompleteResult<T>

    public companion object
}

public fun <T> Autocomplete(
    service: AutocompleteService<T>,
    options: AutocompleteOptions = AutocompleteOptions(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<T> = DefaultAutocomplete(service, options, dispatcher)