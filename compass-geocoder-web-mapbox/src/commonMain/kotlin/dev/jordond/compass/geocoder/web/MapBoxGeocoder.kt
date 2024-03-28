@file:Suppress("FunctionName")

package dev.jordond.compass.geocoder.web

import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.web.parameter.MapBoxParameters
import dev.jordond.compass.geocoder.web.parameter.MapBoxParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.mapBoxParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

/**
 * Creates a new [Geocoder] using the MapBox HTTP API geocoding service.
 *
 * See [MapBox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The MapBox API key.
 * @param parameters The parameters to use for the geocoder.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @return A new [Geocoder] using the MapBox HTTP API geocoding service.
 */
public fun MapBoxGeocoder(
    apiKey: String,
    parameters: MapBoxParameters = MapBoxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder {
    val platform = MapBoxPlatformGeocoder(apiKey, parameters, json, client)
    return Geocoder(platform, dispatcher)
}

/**
 * Creates a new [Geocoder] using the MapBox HTTP API geocoding service.
 *
 * See [MapBox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The MapBox API key.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @param block Customize the [MapBoxParameters] to use for the geocoder.
 * @return A new [Geocoder] using the MapBox HTTP API geocoding service.
 */
public fun MapBoxGeocoder(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: MapBoxParametersBuilder.() -> Unit,
): Geocoder = MapBoxGeocoder(apiKey, mapBoxParameters(block), json, client, dispatcher)

/**
 * Creates a new [Geocoder] using the MapBox HTTP API geocoding service.
 *
 * See [MapBox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The MapBox API key.
 * @param parameters The parameters to use for the geocoder.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @return A new [Geocoder] using the MapBox HTTP API geocoding service.
 */
public fun Geocoder.Companion.mapBox(
    apiKey: String,
    parameters: MapBoxParameters = MapBoxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = MapBoxGeocoder(apiKey, parameters, json, client, dispatcher)

/**
 * Creates a new [Geocoder] using the MapBox HTTP API geocoding service.
 *
 * See [MapBox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The MapBox API key.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @param block Customize the [MapBoxParameters] to use for the geocoder.
 * @return A new [Geocoder] using the MapBox HTTP API geocoding service.
 */
public fun Geocoder.Companion.mapBox(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: MapBoxParametersBuilder.() -> Unit,
): Geocoder = MapBoxGeocoder(apiKey, mapBoxParameters(block), json, client, dispatcher)