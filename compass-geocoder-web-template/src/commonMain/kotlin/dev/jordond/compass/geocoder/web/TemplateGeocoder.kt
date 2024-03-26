@file:Suppress("FunctionName")

package dev.jordond.compass.geocoder.web

import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.web.parameter.TemplateParameters
import dev.jordond.compass.geocoder.web.parameter.TemplateParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.templateParameters
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

/**
 * Creates a new [Geocoder] using the TEMPLATE HTTP API geocoding service.
 *
 * See [TEMPLATE]() for more information.
 *
 * @param apiKey The TEMPLATE API key.
 * @param parameters The parameters to use for the geocoder.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @return A new [Geocoder] using the TEMPLATE HTTP API geocoding service.
 */
public fun TemplateGeocoder(
    apiKey: String,
    parameters: TemplateParameters = TemplateParameters(),
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder {
    val platform = TemplatePlatformGeocoder(apiKey, parameters, json, client)
    return Geocoder(platform, dispatcher)
}

/**
 * Creates a new [Geocoder] using the TEMPLATE HTTP API geocoding service.
 *
 * See [TEMPLATE]() for more information.
 *
 * @param apiKey The TEMPLATE API key.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @param block Customize the [TemplateParameters] to use for the geocoder.
 * @return A new [Geocoder] using the TEMPLATE HTTP API geocoding service.
 */
public fun TemplateGeocoder(
    apiKey: String,
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: TemplateParametersBuilder.() -> Unit,
): Geocoder = TemplateGeocoder(apiKey, templateParameters(block), json, client, dispatcher)

/**
 * Creates a new [Geocoder] using the TEMPLATE HTTP API geocoding service.
 *
 * See [TEMPLATE]() for more information.
 *
 * @param apiKey The TEMPLATE API key.
 * @param parameters The parameters to use for the geocoder.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @return A new [Geocoder] using the TEMPLATE HTTP API geocoding service.
 */
public fun Geocoder.Companion.template(
    apiKey: String,
    parameters: TemplateParameters = TemplateParameters(),
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = TemplateGeocoder(apiKey, parameters, json, client, dispatcher)

/**
 * Creates a new [Geocoder] using the TEMPLATE HTTP API geocoding service.
 *
 * See [TEMPLATE]() for more information.
 *
 * @param apiKey The TEMPLATE API key.
 * @param json The JSON implementation to use for serialization.
 * @param client The HTTP client to use for requests.
 * @param dispatcher The coroutine dispatcher to use for requests.
 * @param block Customize the [TemplateParameters] to use for the geocoder.
 * @return A new [Geocoder] using the TEMPLATE HTTP API geocoding service.
 */
public fun Geocoder.Companion.template(
    apiKey: String,
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: TemplateParametersBuilder.() -> Unit,
): Geocoder = TemplateGeocoder(apiKey, json, client, dispatcher, block)