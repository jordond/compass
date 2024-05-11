package dev.jordond.compass.geocoder.web

import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.geocoder.web.parameter.OpenCageParameters
import dev.jordond.compass.geocoder.web.parameter.OpenCageParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.openCageParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

/**
 * Defines a [HttpApiPlatformGeocoder] that uses the OpenCage Geocoding API.
 *
 * See [OpenCage](https://opencagedata.com/api)
 * for more information.
 */
public interface OpenCageGeocoder : HttpApiPlatformGeocoder {

    // TODO
    public companion object {

        private const val BASE_URL = ""

        private fun createUrl(
            target: String,
            apiKey: String,
            params: OpenCageParameters,
        ): String = "$BASE_URL?$target&${params.encode()}key=$apiKey"

        internal fun forwardUrl(address: String, apiKey: String, params: OpenCageParameters) =
            createUrl(target = "address=$address", apiKey = apiKey, params = params)

        internal fun reverseUrl(
            latitude: Double,
            longitude: Double,
            apiKey: String,
            params: OpenCageParameters,
        ) = createUrl(target = "latlng=$latitude,$longitude", apiKey = apiKey, params = params)
    }
}

/**
 * Creates a [OpenCagePlatformGeocoder] to be used with the [Geocoder].
 *
 * See [OpenCage](https://opencagedata.com/api) for more information.
 *
 * @param apiKey The OpenCage API key.
 * @param parameters The parameters to use for the geocoding requests.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @return A [OpenCagePlatformGeocoder] instance.
 */
public fun OpenCagePlatformGeocoder(
    apiKey: String,
    parameters: OpenCageParameters = OpenCageParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): OpenCageGeocoder {
    val delegate = HttpApiPlatformGeocoder(
        forwardEndpoint = OpenCageForwardEndpoint(apiKey, parameters),
        reverseEndpoint = OpenCageReverseEndpoint(apiKey, parameters),
        json = json,
        client = client,
    )
    return object : HttpApiPlatformGeocoder by delegate, OpenCageGeocoder {}
}

/**
 * Creates a [OpenCagePlatformGeocoder] to be used with the [Geocoder].
 *
 * See [OpenCage](https://opencagedata.com/api) for more information.
 *
 * @param apiKey The OpenCage API key.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @param block A lambda that configures the [OpenCageParameters] to use for the geocoding requests.
 * @return A [OpenCagePlatformGeocoder] instance.
 */
public fun OpenCagePlatformGeocoder(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    block: OpenCageParametersBuilder.() -> Unit,
): OpenCageGeocoder = OpenCagePlatformGeocoder(apiKey, openCageParameters(block), json, client)

/**
 * Creates a [OpenCagePlatformGeocoder] to be used with the [Geocoder].
 *
 * See [OpenCage](https://opencagedata.com/api) for more information.
 *
 * @param apiKey The OpenCage API key.
 * @param parameters The parameters to use for the geocoding requests.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @return A [OpenCagePlatformGeocoder] instance.
 */
public fun PlatformGeocoder.Companion.openCage(
    apiKey: String,
    parameters: OpenCageParameters = OpenCageParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): OpenCageGeocoder = OpenCagePlatformGeocoder(apiKey, parameters, json, client)

/**
 * Creates a [OpenCagePlatformGeocoder] to be used with the [Geocoder].
 *
 * See [OpenCage](https://opencagedata.com/api) for more information.
 *
 * @param apiKey The OpenCage API key.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @param block A lambda that configures the [OpenCageParameters] to use for the geocoding requests.
 * @return A [OpenCagePlatformGeocoder] instance.
 */
public fun PlatformGeocoder.Companion.openCage(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    block: OpenCageParametersBuilder.() -> Unit,
): OpenCageGeocoder = OpenCagePlatformGeocoder(apiKey, openCageParameters(block), json, client)

