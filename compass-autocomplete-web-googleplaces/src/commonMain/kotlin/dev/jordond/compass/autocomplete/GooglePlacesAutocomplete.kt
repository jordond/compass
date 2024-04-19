@file:Suppress("FunctionName")

import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteOptions
import dev.jordond.compass.autocomplete.QueryResult
import dev.jordond.compass.autocomplete.web.GooglePlacesAutocompleteService
import dev.jordond.compass.autocomplete.web.parameter.GooglePlacesParameters
import dev.jordond.compass.autocomplete.web.parameter.GooglePlacesParametersBuilder
import dev.jordond.compass.autocomplete.web.parameter.googlePlacesParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

public fun Autocomplete(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    parameters: GooglePlacesParameters = GooglePlacesParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<QueryResult> =
    GooglePlacesAutocomplete(apiKey, options, parameters, json, client, dispatcher)

public fun GooglePlacesAutocomplete(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    parameters: GooglePlacesParameters = GooglePlacesParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<QueryResult> {
    val service = GooglePlacesAutocompleteService(apiKey, parameters, json, client)
    return Autocomplete(service, options, dispatcher)
}

public fun GooglePlacesAutocomplete(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: GooglePlacesParametersBuilder.() -> Unit,
): Autocomplete<QueryResult> = GooglePlacesAutocomplete(
    apiKey = apiKey,
    options = options,
    parameters = googlePlacesParameters(block),
    json = json,
    client = client,
    dispatcher = dispatcher,
)

public fun Autocomplete.Companion.googlePlaces(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    parameters: GooglePlacesParameters = GooglePlacesParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<QueryResult> =
    GooglePlacesAutocomplete(apiKey, options, parameters, json, client, dispatcher)

public fun Autocomplete.Companion.googlePlaces(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: GooglePlacesParametersBuilder.() -> Unit,
): Autocomplete<QueryResult> = GooglePlacesAutocomplete(apiKey, options, json, client, dispatcher, block)