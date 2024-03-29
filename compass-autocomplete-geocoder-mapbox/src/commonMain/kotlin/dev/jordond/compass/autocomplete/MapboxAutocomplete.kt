@file:Suppress("FunctionName")

package dev.jordond.compass.autocomplete

import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.web.MapboxGeocoderAutocompleteService
import dev.jordond.compass.geocoder.web.parameter.MapboxParameters
import dev.jordond.compass.geocoder.web.parameter.MapboxParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.mapboxParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

/**
 * Creates a new [Autocomplete] instance that uses the Mapbox Geocoding API.
 *
 * @param apiKey The Mapbox API key.
 * @param options The autocomplete options.
 * @param parameters The Mapbox geocoder parameters.
 * @param json The JSON serializer.
 * @param client The HTTP client to make the requests with.
 * @param dispatcher The coroutine dispatcher.
 * @return A new [Autocomplete] instance.
 */
public fun Autocomplete(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<Place> {
    return MapboxGeocoderAutocomplete(apiKey, options, parameters, json, client, dispatcher)
}

/**
 * Creates a new [Autocomplete] instance that uses the Mapbox Geocoding API.
 *
 * @param apiKey The Mapbox API key.
 * @param options The autocomplete options.
 * @param json The JSON serializer.
 * @param client The HTTP client to make the requests with.
 * @param dispatcher The coroutine dispatcher.
 * @param block The builder block to configure the Mapbox geocoder parameters.
 * @return A new [Autocomplete] instance.
 */
public fun Autocomplete(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: MapboxParametersBuilder.() -> Unit,
): Autocomplete<Place> {
    return MapboxGeocoderAutocomplete(apiKey, options, json, client, dispatcher, block)
}

/**
 * Creates a new [Autocomplete] instance that uses the Mapbox Geocoding API.
 *
 * @param apiKey The Mapbox API key.
 * @param options The autocomplete options.
 * @param parameters The Mapbox geocoder parameters.
 * @param json The JSON serializer.
 * @param client The HTTP client to make the requests with.
 * @param dispatcher The coroutine dispatcher.
 * @return A new [Autocomplete] instance.
 */
public fun MapboxGeocoderAutocomplete(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<Place> {
    val service = MapboxGeocoderAutocompleteService(apiKey, parameters, json, client)
    return Autocomplete(service, options, dispatcher)
}

/**
 * Creates a new [Autocomplete] instance that uses the Mapbox Geocoding API.
 *
 * @param apiKey The Mapbox API key.
 * @param options The autocomplete options.
 * @param json The JSON serializer.
 * @param client The HTTP client to make the requests with.
 * @param dispatcher The coroutine dispatcher.
 * @param block The builder block to configure the Mapbox geocoder parameters.
 * @return A new [Autocomplete] instance.
 */
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

/**
 * Creates a new [Autocomplete] instance that uses the Mapbox Geocoding API.
 *
 * @param apiKey The Mapbox API key.
 * @param options The autocomplete options.
 * @param parameters The Mapbox geocoder parameters.
 * @param json The JSON serializer.
 * @param client The HTTP client to make the requests with.
 * @param dispatcher The coroutine dispatcher.
 * @return A new [Autocomplete] instance.
 */
public fun Autocomplete.Companion.mapboxGeocoder(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<Place> =
    MapboxGeocoderAutocomplete(apiKey, options, parameters, json, client, dispatcher)

/**
 * Creates a new [Autocomplete] instance that uses the Mapbox Geocoding API.
 *
 * @param apiKey The Mapbox API key.
 * @param options The autocomplete options.
 * @param json The JSON serializer.
 * @param client The HTTP client to make the requests with.
 * @param dispatcher The coroutine dispatcher.
 * @param block The builder block to configure the Mapbox geocoder parameters.
 * @return A new [Autocomplete] instance.
 */
public fun Autocomplete.Companion.mapboxGeocoder(
    apiKey: String,
    options: AutocompleteOptions = AutocompleteOptions(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: MapboxParametersBuilder.() -> Unit,
): Autocomplete<Place> =
    MapboxGeocoderAutocomplete(apiKey, options, json, client, dispatcher, block)