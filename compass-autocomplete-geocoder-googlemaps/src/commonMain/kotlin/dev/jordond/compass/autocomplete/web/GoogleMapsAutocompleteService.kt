@file:Suppress("FunctionName")

package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.AutocompleteService
import dev.jordond.compass.geocoder.web.GoogleMapsForwardEndpoint
import dev.jordond.compass.geocoder.web.google.internal.GeocodeResponse
import dev.jordond.compass.geocoder.web.google.internal.resultsOrThrow
import dev.jordond.compass.geocoder.web.google.internal.toPlaces
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsParameters
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.googleMapsParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import dev.jordond.compass.tools.web.makeRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import kotlinx.serialization.json.Json

public fun GoogleMapsAutocompleteService(
    apiKey: String,
    parameters: GoogleMapsParameters = GoogleMapsParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): AutocompleteService<Place> {
    val delegateEndpoint = GoogleMapsForwardEndpoint(apiKey, parameters)
    val endpoint = HttpApiEndpoint(
        url = delegateEndpoint::url,
        mapResponse = { response -> response.body<GeocodeResponse>().resultsOrThrow().toPlaces() }
    )

    return object : AutocompleteService<Place> {
        override fun isAvailable(): Boolean = apiKey.isNotBlank()

        override suspend fun search(query: String): List<Place> {
            val url = endpoint.url(query)
            return client.makeRequest(url, endpoint::mapResponse)
        }
    }
}

public fun GoogleMapsAutocompleteService(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    block: GoogleMapsParametersBuilder.() -> Unit,
): AutocompleteService<Place> =
    GoogleMapsAutocompleteService(apiKey, googleMapsParameters(block), json, client)

public fun AutocompleteService.Companion.googleMaps(
    apiKey: String,
    parameters: GoogleMapsParameters = GoogleMapsParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): AutocompleteService<Place> =
    GoogleMapsAutocompleteService(apiKey, parameters, json, client)

public fun AutocompleteService.Companion.googleMaps(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    block: GoogleMapsParametersBuilder.() -> Unit,
): AutocompleteService<Place> =
    GoogleMapsAutocompleteService(apiKey, googleMapsParameters(block), json, client)
