package dev.jordond.compass.geocoder.web

import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.geocoder.web.parameter.TemplateParameters
import dev.jordond.compass.geocoder.web.parameter.TemplateParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.templateParameters
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

/**
 * Defines a [HttpApiPlatformGeocoder] that uses the TEMPLATE Geocoding API.
 *
 * See [TEMPLATE]()
 * for more information.
 */
public interface TemplatePlatformGeocoder : HttpApiPlatformGeocoder {

    // Modify the following to match your API.
    public companion object {

        private const val BASE_URL = ""

        private fun createUrl(
            target: String,
            apiKey: String,
            params: TemplateParameters,
        ): String = "$BASE_URL?$target&${params.encode()}key=$apiKey"

        internal fun forwardUrl(address: String, apiKey: String, params: TemplateParameters) =
            createUrl(target = "address=$address", apiKey = apiKey, params = params)

        internal fun reverseUrl(
            latitude: Double,
            longitude: Double,
            apiKey: String,
            params: TemplateParameters,
        ) = createUrl(target = "latlng=$latitude,$longitude", apiKey = apiKey, params = params)
    }
}

/**
 * Creates a [TemplatePlatformGeocoder] to be used with the [Geocoder].
 *
 * See [TEMPLATE]() for more information.
 *
 * @param apiKey The TEMPLATE API key.
 * @param parameters The parameters to use for the geocoding requests.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @return A [TemplatePlatformGeocoder] instance.
 */
public fun TemplatePlatformGeocoder(
    apiKey: String,
    parameters: TemplateParameters = TemplateParameters(),
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
): TemplatePlatformGeocoder {
    val delegate = HttpApiPlatformGeocoder(
        forwardEndpoint = TemplateForwardEndpoint(apiKey, parameters),
        reverseEndpoint = TemplateReverseEndpoint(apiKey, parameters),
        json = json,
        client = client,
    )
    return object : HttpApiPlatformGeocoder by delegate, TemplatePlatformGeocoder {}
}

/**
 * Creates a [TemplatePlatformGeocoder] to be used with the [Geocoder].
 *
 * See [TEMPLATE]() for more information.
 *
 * @param apiKey The TEMPLATE API key.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @param block A lambda that configures the [TemplateParameters] to use for the geocoding requests.
 * @return A [TemplatePlatformGeocoder] instance.
 */
public fun TemplatePlatformGeocoder(
    apiKey: String,
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
    block: TemplateParametersBuilder.() -> Unit,
): TemplatePlatformGeocoder =
    TemplatePlatformGeocoder(apiKey, templateParameters(block), json, client)

/**
 * Creates a [TemplatePlatformGeocoder] to be used with the [Geocoder].
 *
 * See [TEMPLATE]() for more information.
 *
 * @param apiKey The TEMPLATE API key.
 * @param parameters The parameters to use for the geocoding requests.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @return A [TemplatePlatformGeocoder] instance.
 */
public fun PlatformGeocoder.Companion.template(
    apiKey: String,
    parameters: TemplateParameters = TemplateParameters(),
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
): TemplatePlatformGeocoder =
    TemplatePlatformGeocoder(apiKey, parameters, json, client)

/**
 * Creates a [TemplatePlatformGeocoder] to be used with the [Geocoder].
 *
 * See [TEMPLATE]() for more information.
 *
 * @param apiKey The TEMPLATE API key.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @param block A lambda that configures the [TemplateParameters] to use for the geocoding requests.
 * @return A [TemplatePlatformGeocoder] instance.
 */
public fun PlatformGeocoder.Companion.template(
    apiKey: String,
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
    block: TemplateParametersBuilder.() -> Unit,
): TemplatePlatformGeocoder =
    TemplatePlatformGeocoder(apiKey, templateParameters(block), json, client)

