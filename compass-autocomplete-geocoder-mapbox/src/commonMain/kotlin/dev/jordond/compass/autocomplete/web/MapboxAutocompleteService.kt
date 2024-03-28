@file:Suppress("FunctionName")

package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.AutocompleteService
import dev.jordond.compass.geocoder.web.MapboxForwardEndpoint
import dev.jordond.compass.geocoder.web.mapbox.internal.GeocodeResponse
import dev.jordond.compass.geocoder.web.mapbox.internal.toPlaces
import dev.jordond.compass.geocoder.web.parameter.MapboxParameters
import dev.jordond.compass.geocoder.web.parameter.MapboxParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.mapboxParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import dev.jordond.compass.tools.web.makeRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import kotlinx.serialization.json.Json

/**
 * Creates a new [AutocompleteService] that uses the Mapbox Geocoding API to provide
 * autocomplete suggestions.
 *
 * @param apiKey The Mapbox API key.
 * @param parameters The parameters to use when making requests to the Mapbox Geocoding API.
 * @param json The JSON serializer to use when parsing responses.
 * @param client The HTTP client to use when making requests.
 * @return A new [AutocompleteService] that uses the Mapbox Geocoding API.
 */
public fun MapboxGeocoderAutocompleteService(
    apiKey: String,
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): AutocompleteService<Place> {
    val delegateEndpoint = MapboxForwardEndpoint(apiKey, parameters)
    val endpoint = HttpApiEndpoint(
        url = delegateEndpoint::url,
        mapResponse = { response -> response.body<GeocodeResponse>().toPlaces() }
    )

    return object : AutocompleteService<Place> {
        override fun isAvailable(): Boolean = apiKey.isNotBlank()

        override suspend fun search(query: String): List<Place> {
            val url = endpoint.url(query)
            return client.makeRequest(url, endpoint::mapResponse)
        }
    }
}

/**
 * Creates a new [AutocompleteService] that uses the Mapbox Geocoding API to provide
 * autocomplete suggestions.
 *
 * @param apiKey The Mapbox API key.
 * @param json The JSON serializer to use when parsing responses.
 * @param client The HTTP client to use when making requests.
 * @param block A block that configures the parameters to use when making requests to the
 * Mapbox Geocoding API.
 * @return A new [AutocompleteService] that uses the Mapbox Geocoding API.
 */
public fun MapboxGeocoderAutocompleteService(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    block: MapboxParametersBuilder.() -> Unit,
): AutocompleteService<Place> =
    MapboxGeocoderAutocompleteService(apiKey, mapboxParameters(block), json, client)

/**
 * Creates a new [AutocompleteService] that uses the Mapbox Geocoding API to provide
 * autocomplete suggestions.
 *
 * @param apiKey The Mapbox API key.
 * @param parameters The parameters to use when making requests to the Mapbox Geocoding API.
 * @param json The JSON serializer to use when parsing responses.
 * @param client The HTTP client to use when making requests.
 * @return A new [AutocompleteService] that uses the Mapbox Geocoding API.
 */
public fun AutocompleteService.Companion.mapboxGeocoder(
    apiKey: String,
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): AutocompleteService<Place> =
    MapboxGeocoderAutocompleteService(apiKey, parameters, json, client)

/**
 * Creates a new [AutocompleteService] that uses the Mapbox Geocoding API to provide
 * autocomplete suggestions.
 *
 * @param apiKey The Mapbox API key.
 * @param json The JSON serializer to use when parsing responses.
 * @param client The HTTP client to use when making requests.
 * @param block A block that configures the parameters to use when making requests to the
 * Mapbox Geocoding API.
 * @return A new [AutocompleteService] that uses the Mapbox Geocoding API.
 */
public fun AutocompleteService.Companion.mapboxGeocoder(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    block: MapboxParametersBuilder.() -> Unit,
): AutocompleteService<Place> =
    MapboxGeocoderAutocompleteService(apiKey, mapboxParameters(block), json, client)
