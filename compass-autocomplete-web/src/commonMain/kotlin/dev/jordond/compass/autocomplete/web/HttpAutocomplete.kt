package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteOptions
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

/**
 * Creates a new [Autocomplete] instance that uses a HTTP service to provide autocomplete
 * suggestions.
 *
 * @param T The type of the autocomplete suggestions results.
 * @param httpService The HTTP service to use for autocomplete suggestions.
 * @param options The options to use for the autocomplete.
 * @param dispatcher The coroutine dispatcher to use for the autocomplete.
 * @return A new [Autocomplete] instance.
 */
public fun <T> Autocomplete(
    httpService: HttpAutocompleteService<T>,
    options: AutocompleteOptions = AutocompleteOptions(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<T> = Autocomplete(httpService, options, dispatcher)

/**
 * Creates a new [Autocomplete] instance that uses a HTTP service to provide autocomplete
 * suggestions.
 *
 * @param T The type of the autocomplete suggestions results.
 * @param endpoint The search endpoint to use for autocomplete suggestions.
 * @param options The options to use for the autocomplete.
 * @param json The JSON serializer to use for the autocomplete.
 * @param httpClient The HTTP client to use for the autocomplete.
 * @param dispatcher The coroutine dispatcher to use for the autocomplete.
 * @return A new [Autocomplete] instance.
 */
public fun <T> Autocomplete(
    endpoint: SearchEndpoint<T>,
    options: AutocompleteOptions = AutocompleteOptions(),
    json: Json = HttpApiEndpoint.json(),
    httpClient: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<T> {
    val service = HttpAutocompleteService(endpoint, json, httpClient)
    return Autocomplete(service, options, dispatcher)
}