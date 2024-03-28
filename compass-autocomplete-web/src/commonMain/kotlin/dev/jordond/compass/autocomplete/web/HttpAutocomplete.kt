package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteOptions
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

public fun <T> Autocomplete(
    httpService: HttpAutocompleteService<T>,
    options: AutocompleteOptions = AutocompleteOptions(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<T> = Autocomplete(httpService, options, dispatcher)

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