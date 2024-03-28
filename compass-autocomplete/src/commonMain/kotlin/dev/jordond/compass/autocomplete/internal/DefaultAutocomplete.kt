package dev.jordond.compass.autocomplete.internal

import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteOptions
import dev.jordond.compass.autocomplete.AutocompleteResult
import dev.jordond.compass.autocomplete.AutocompleteService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class DefaultAutocomplete<T>(
    private val service: AutocompleteService<T>,
    override val options: AutocompleteOptions,
    private val dispatcher: CoroutineDispatcher,
) : Autocomplete<T> {

    override suspend fun search(query: String): AutocompleteResult<T> {
        if (!service.isAvailable()) {
            return AutocompleteResult.NotSupported
        }

        try {
            if (query.length <= options.minimumQuery) {
                return AutocompleteResult.Success(emptyList())
            }

            val result = withContext(dispatcher) {
                service.search(query)
            }

            return AutocompleteResult.Success(result)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (cause: Throwable) {
            return AutocompleteResult.Failed(cause.message ?: "An unknown error occurred")
        }
    }
}