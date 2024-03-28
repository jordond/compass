@file:Suppress("FunctionName")

package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteOptions
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsParameters
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.googleMapsParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

public fun GoogleMapsAutocomplete(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    parameters: GoogleMapsParameters = GoogleMapsParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<Place> {
    val service = GoogleMapsAutocompleteService(apiKey, parameters, json, client)
    return Autocomplete(service, options, dispatcher)
}

public fun GoogleMapsAutocomplete(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: GoogleMapsParametersBuilder.() -> Unit,
): Autocomplete<Place> =
    GoogleMapsAutocomplete(apiKey, options, googleMapsParameters(block), json, client, dispatcher)


public fun Autocomplete.Companion.googleMaps(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    parameters: GoogleMapsParameters = GoogleMapsParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<Place> =
    GoogleMapsAutocomplete(apiKey, options, parameters, json, client, dispatcher)

public fun Autocomplete.Companion.googleMaps(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: GoogleMapsParametersBuilder.() -> Unit,
): Autocomplete<Place> = GoogleMapsAutocomplete(apiKey, options, json, client, dispatcher, block)