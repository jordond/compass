package dev.jordond.compass.geocoder.web

import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.geocoder.web.parameter.MapboxParameters
import dev.jordond.compass.geocoder.web.parameter.MapboxParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.mapboxParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

/**
 * Defines a [HttpApiPlatformGeocoder] that uses the Mapbox Geocoding API.
 *
 * See [Mapbox](https://www.mapbox.com/) for more information.
 */
public interface MapboxPlatformGeocoder : HttpApiPlatformGeocoder {

    public companion object {

        private const val BASE_URL = "https://api.mapbox.com/search/geocode/v6"

        private fun createUrl(target: String, apiKey: String, params: MapboxParameters): String =
            "$BASE_URL/$target&${params.encode()}&access_token=$apiKey"

        internal fun forwardUrl(query: String, apiKey: String, params: MapboxParameters) =
            createUrl("forward?q=$query", apiKey, params)

        internal fun reverseUrl(
            latitude: Double,
            longitude: Double,
            apiKey: String,
            params: MapboxParameters,
        ) = createUrl(
            target = "reverse?latitude=$latitude&longitude=$longitude",
            apiKey = apiKey,
            params = params,
        )
    }
}

/**
 * Creates a [MapboxPlatformGeocoder] to be used with the [Geocoder].
 *
 * See [Mapbox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The Mapbox API key.
 * @param parameters The parameters to use for the geocoding requests.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @return A [MapboxPlatformGeocoder] instance.
 */
public fun MapboxPlatformGeocoder(
    apiKey: String,
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): MapboxPlatformGeocoder {
    val delegate = HttpApiPlatformGeocoder(
        forwardEndpoint = MapboxForwardEndpoint(apiKey, parameters),
        reverseEndpoint = MapboxReverseEndpoint(apiKey, parameters),
        json = json,
        client = client,
    )
    return object : HttpApiPlatformGeocoder by delegate, MapboxPlatformGeocoder {}
}

/**
 * Creates a [MapboxPlatformGeocoder] to be used with the [Geocoder].
 *
 * See [Mapbox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The Mapbox API key.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @param block A lambda that configures the [MapboxParameters] to use for the geocoding requests.
 * @return A [MapboxPlatformGeocoder] instance.
 */
public fun MapboxPlatformGeocoder(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    block: MapboxParametersBuilder.() -> Unit,
): MapboxPlatformGeocoder = MapboxPlatformGeocoder(apiKey, mapboxParameters(block), json, client)

/**
 * Creates a [MapboxPlatformGeocoder] to be used with the [Geocoder].
 *
 * See [Mapbox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The Mapbox API key.
 * @param parameters The parameters to use for the geocoding requests.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @return A [MapboxPlatformGeocoder] instance.
 */
public fun PlatformGeocoder.Companion.mapbox(
    apiKey: String,
    parameters: MapboxParameters = MapboxParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): MapboxPlatformGeocoder =
    MapboxPlatformGeocoder(apiKey, parameters, json, client)

/**
 * Creates a [MapboxPlatformGeocoder] to be used with the [Geocoder].
 *
 * See [Mapbox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The Mapbox API key.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @param block A lambda that configures the [MapboxParameters] to use for the geocoding requests.
 * @return A [MapboxPlatformGeocoder] instance.
 */
public fun PlatformGeocoder.Companion.mapbox(
    apiKey: String,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
    block: MapboxParametersBuilder.() -> Unit,
): MapboxPlatformGeocoder = MapboxPlatformGeocoder(apiKey, mapboxParameters(block), json, client)
