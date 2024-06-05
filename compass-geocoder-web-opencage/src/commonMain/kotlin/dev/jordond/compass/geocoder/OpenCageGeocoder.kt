@file:Suppress("FunctionName")

package dev.jordond.compass.geocoder

import dev.jordond.compass.geocoder.web.OpenCagePlatformGeocoder
import dev.jordond.compass.geocoder.web.parameter.OpenCageParameters
import dev.jordond.compass.geocoder.web.parameter.OpenCageParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.openCageParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

/**
 * Creates a new [Geocoder] using the OpenCage HTTP API geocoding service.
 *
 * See [OpenCage](https://opencagedata.com/api) for more information.
 *
 * @param apiKey The OpenCage API key.
 * @param parameters The parameters to use for the geocoder.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @return A new [Geocoder] using the OpenCage HTTP API geocoding service.
 */
public fun Geocoder(
    apiKey: String,
    parameters: OpenCageParameters = OpenCageParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder {
    return OpenCageGeocoder(apiKey, parameters, json, client, dispatcher)
}

/**
 * Creates a new [Geocoder] using the OpenCage HTTP API geocoding service.
 *
 * See [OpenCage](https://opencagedata.com/api) for more information.
 *
 * @param apiKey The OpenCage API key.
 * @param parameters The parameters to use for the geocoder.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @return A new [Geocoder] using the OpenCage HTTP API geocoding service.
 */
public fun OpenCageGeocoder(
    apiKey: String,
    parameters: OpenCageParameters = OpenCageParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder {
    val platform = OpenCagePlatformGeocoder(apiKey, parameters, json, client)
    return Geocoder(platform, dispatcher)
}

/**
 * Creates a new [Geocoder] using the OpenCage HTTP API geocoding service.
 *
 * See [OpenCage](https://opencagedata.com/api) for more information.
 *
 * @param apiKey The OpenCage API key.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @param block Customize the [OpenCageParameters] to use for the geocoder.
 * @return A new [Geocoder] using the OpenCage HTTP API geocoding service.
 */
public fun OpenCageGeocoder(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: OpenCageParametersBuilder.() -> Unit,
): Geocoder = OpenCageGeocoder(apiKey, openCageParameters(block), json, client, dispatcher)

/**
 * Creates a new [Geocoder] using the OpenCage HTTP API geocoding service.
 *
 * See [OpenCage](https://opencagedata.com/api) for more information.
 *
 * @param apiKey The OpenCage API key.
 * @param parameters The parameters to use for the geocoder.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @return A new [Geocoder] using the OpenCage HTTP API geocoding service.
 */
public fun Geocoder.Companion.openCage(
    apiKey: String,
    parameters: OpenCageParameters = OpenCageParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = OpenCageGeocoder(apiKey, parameters, json, client, dispatcher)

/**
 * Creates a new [Geocoder] using the OpenCage HTTP API geocoding service.
 *
 * See [OpenCage](https://opencagedata.com/api) for more information.
 *
 * @param apiKey The OpenCage API key.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @param block Customize the [OpenCageParameters] to use for the geocoder.
 * @return A new [Geocoder] using the OpenCage HTTP API geocoding service.
 */
public fun Geocoder.Companion.openCage(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: OpenCageParametersBuilder.() -> Unit,
): Geocoder = OpenCageGeocoder(apiKey, json, client, dispatcher, block)