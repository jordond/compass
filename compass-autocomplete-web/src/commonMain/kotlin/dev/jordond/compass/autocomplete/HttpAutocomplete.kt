package dev.jordond.compass.autocomplete

import dev.jordond.compass.autocomplete.web.DetailsEndpoint
import dev.jordond.compass.autocomplete.web.HttpAutocompleteService
import dev.jordond.compass.autocomplete.web.SearchEndpoint
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
public fun Autocomplete(
    httpService: HttpAutocompleteService,
    options: AutocompleteOptions = AutocompleteOptions(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<QueryResult> = Autocomplete(httpService, options, dispatcher)

/**
 * Creates a new [Autocomplete] instance that uses a HTTP service to provide autocomplete
 * suggestions.
 *
 * @param T The type of the autocomplete suggestions results.
 * @param search The search endpoint to use for autocomplete suggestions.
 * @param details The details endpoint to use for autocomplete suggestions.
 * @param options The options to use for the autocomplete.
 * @param json The JSON serializer to use for the autocomplete.
 * @param httpClient The HTTP client to use for the autocomplete.
 * @param dispatcher The coroutine dispatcher to use for the autocomplete.
 * @return A new [Autocomplete] instance.
 */
public fun Autocomplete(
    search: SearchEndpoint,
    details: DetailsEndpoint,
    options: AutocompleteOptions = AutocompleteOptions(),
    json: Json = HttpApiEndpoint.json(),
    httpClient: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<QueryResult> {
    val service = HttpAutocompleteService(search, details, json, httpClient)
    return Autocomplete(service, options, dispatcher)
}