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

public fun GoogleMapsGeocoderAutocompleteService(
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

public fun GoogleMapsGeocoderAutocompleteService(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    block: MapboxParametersBuilder.() -> Unit,
): AutocompleteService<Place> =
    GoogleMapsGeocoderAutocompleteService(apiKey, mapboxParameters(block), json, client)

public fun AutocompleteService.Companion.mapboxGeocoder(
    apiKey: String,
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): AutocompleteService<Place> =
    GoogleMapsGeocoderAutocompleteService(apiKey, parameters, json, client)

public fun AutocompleteService.Companion.mapboxGeocoder(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    block: MapboxParametersBuilder.() -> Unit,
): AutocompleteService<Place> =
    GoogleMapsGeocoderAutocompleteService(apiKey, mapboxParameters(block), json, client)
