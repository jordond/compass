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

/**
 * Creates a new [AutocompleteService] that uses the Google Maps Geocoding API to provide
 * autocomplete suggestions.
 *
 * @param apiKey The Google Maps API key.
 * @param parameters The parameters to use when making requests to the Google Maps Geocoding API.
 * @param json The JSON serializer to use when parsing responses.
 * @param client The HTTP client to use when making requests.
 * @return A new [AutocompleteService] that uses the Google Maps Geocoding API.
 */
public fun GoogleMapsGeocoderAutocompleteService(
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

/**
 * Creates a new [AutocompleteService] that uses the Google Maps Geocoding API to provide
 * autocomplete suggestions.
 *
 * @param apiKey The Google Maps API key.
 * @param json The JSON serializer to use when parsing responses.
 * @param client The HTTP client to use when making requests.
 * @param block A block that configures the parameters to use when making requests to the
 * Google Maps Geocoding API.
 * @return A new [AutocompleteService] that uses the Google Maps Geocoding API.
 */
public fun GoogleMapsGeocoderAutocompleteService(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    block: GoogleMapsParametersBuilder.() -> Unit,
): AutocompleteService<Place> =
    GoogleMapsGeocoderAutocompleteService(apiKey, googleMapsParameters(block), json, client)

/**
 * Creates a new [AutocompleteService] that uses the Google Maps Geocoding API to provide
 * autocomplete suggestions.
 *
 * @param apiKey The Google Maps API key.
 * @param parameters The parameters to use when making requests to the Google Maps Geocoding API.
 * @param json The JSON serializer to use when parsing responses.
 * @param client The HTTP client to use when making requests.
 * @return A new [AutocompleteService] that uses the Google Maps Geocoding API.
 */
public fun AutocompleteService.Companion.googleMapsGeocoder(
    apiKey: String,
    parameters: GoogleMapsParameters = GoogleMapsParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): AutocompleteService<Place> =
    GoogleMapsGeocoderAutocompleteService(apiKey, parameters, json, client)

/**
 * Creates a new [AutocompleteService] that uses the Google Maps Geocoding API to provide
 * autocomplete suggestions.
 *
 * @param apiKey The Google Maps API key.
 * @param json The JSON serializer to use when parsing responses.
 * @param client The HTTP client to use when making requests.
 * @param block A block that configures the parameters to use when making requests to the
 * Google Maps Geocoding API.
 * @return A new [AutocompleteService] that uses the Google Maps Geocoding API.
 */
public fun AutocompleteService.Companion.googleMapsGeocoder(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    block: GoogleMapsParametersBuilder.() -> Unit,
): AutocompleteService<Place> =
    GoogleMapsGeocoderAutocompleteService(apiKey, googleMapsParameters(block), json, client)
