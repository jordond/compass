@file:Suppress("FunctionName")

package dev.jordond.compass.geocoder

import dev.jordond.compass.geocoder.web.MapboxPlatformGeocoder
import dev.jordond.compass.geocoder.web.parameter.MapboxParameters
import dev.jordond.compass.geocoder.web.parameter.MapboxParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.mapboxParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

/**
 * Creates a new [Geocoder] using the Mapbox HTTP API geocoding service.
 *
 * See [Mapbox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The Mapbox API key.
 * @param parameters The parameters to use for the geocoder.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @return A new [Geocoder] using the Mapbox HTTP API geocoding service.
 */
public fun Geocoder(
    apiKey: String,
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder {
    return MapboxGeocoder(apiKey, parameters, json, client, dispatcher)
}

/**
 * Creates a new [Geocoder] using the Mapbox HTTP API geocoding service.
 *
 * See [Mapbox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The Mapbox API key.
 * @param parameters The parameters to use for the geocoder.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @return A new [Geocoder] using the Mapbox HTTP API geocoding service.
 */
public fun MapboxGeocoder(
    apiKey: String,
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder {
    val platform = MapboxPlatformGeocoder(apiKey, parameters, json, client)
    return Geocoder(platform, dispatcher)
}

/**
 * Creates a new [Geocoder] using the Mapbox HTTP API geocoding service.
 *
 * See [Mapbox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The Mapbox API key.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @param block Customize the [MapboxParameters] to use for the geocoder.
 * @return A new [Geocoder] using the Mapbox HTTP API geocoding service.
 */
public fun MapboxGeocoder(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: MapboxParametersBuilder.() -> Unit,
): Geocoder = MapboxGeocoder(apiKey, mapboxParameters(block), json, client, dispatcher)

/**
 * Creates a new [Geocoder] using the Mapbox HTTP API geocoding service.
 *
 * See [Mapbox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The Mapbox API key.
 * @param parameters The parameters to use for the geocoder.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @return A new [Geocoder] using the Mapbox HTTP API geocoding service.
 */
public fun Geocoder.Companion.mapbox(
    apiKey: String,
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = MapboxGeocoder(apiKey, parameters, json, client, dispatcher)

/**
 * Creates a new [Geocoder] using the Mapbox HTTP API geocoding service.
 *
 * See [Mapbox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The Mapbox API key.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @param block Customize the [MapboxParameters] to use for the geocoder.
 * @return A new [Geocoder] using the Mapbox HTTP API geocoding service.
 */
public fun Geocoder.Companion.mapbox(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: MapboxParametersBuilder.() -> Unit,
): Geocoder = MapboxGeocoder(apiKey, mapboxParameters(block), json, client, dispatcher)