@file:Suppress("FunctionName")

package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteOptions
import dev.jordond.compass.geocoder.web.parameter.MapboxParameters
import dev.jordond.compass.geocoder.web.parameter.MapboxParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.mapboxParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

public fun MapboxGeocoderAutocomplete(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<Place> {
    val service = GoogleMapsGeocoderAutocompleteService(apiKey, parameters, json, client)
    return Autocomplete(service, options, dispatcher)
}

public fun MapboxGeocoderAutocomplete(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: MapboxParametersBuilder.() -> Unit,
): Autocomplete<Place> = MapboxGeocoderAutocomplete(
    apiKey = apiKey,
    options = options,
    parameters = mapboxParameters(block),
    json = json,
    client = client,
    dispatcher = dispatcher,
)


public fun Autocomplete.Companion.mapboxGeocoder(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<Place> =
    MapboxGeocoderAutocomplete(apiKey, options, parameters, json, client, dispatcher)

public fun Autocomplete.Companion.mapboxGeocoder(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: MapboxParametersBuilder.() -> Unit,
): Autocomplete<Place> =
    MapboxGeocoderAutocomplete(apiKey, options, json, client, dispatcher, block)