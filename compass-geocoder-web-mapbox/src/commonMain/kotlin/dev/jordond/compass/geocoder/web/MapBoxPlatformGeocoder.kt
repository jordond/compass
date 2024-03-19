package dev.jordond.compass.geocoder.web

import dev.jordond.compass.geocoder.web.parameters.MapBoxParameters
import dev.jordond.compass.geocoder.web.parameters.MapBoxParametersBuilder
import dev.jordond.compass.geocoder.web.parameters.mapBoxParameters
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

/**
 * Defines a [HttpApiPlatformGeocoder] that uses the MapBox Geocoding API.
 *
 * See [MapBox](https://www.mapbox.com/) for more information.
 */
public interface MapBoxPlatformGeocoder : HttpApiPlatformGeocoder {

    public companion object {

        private const val BASE_URL = "https://api.mapbox.com/search/geocode/v6"

        private fun createUrl(target: String, apiKey: String, params: MapBoxParameters): String =
            "$BASE_URL/$target&${params.encode()}&access_token=$apiKey"

        internal fun forwardUrl(query: String, apiKey: String, params: MapBoxParameters) =
            createUrl("forward?q=$query", apiKey, params)

        internal fun reverseUrl(
            latitude: Double,
            longitude: Double,
            apiKey: String,
            params: MapBoxParameters,
        ) = createUrl(
            target = "reverse?latitude=$latitude&longitude=$longitude",
            apiKey = apiKey,
            params = params,
        )
    }
}

/**
 * Creates a [MapBoxPlatformGeocoder] to be used with the [Geocoder].
 *
 * See [MapBox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The MapBox API key.
 * @param parameters The parameters to use for the geocoding requests.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @return A [MapBoxPlatformGeocoder] instance.
 */
public fun MapBoxPlatformGeocoder(
    apiKey: String,
    parameters: MapBoxParameters = MapBoxParameters(),
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
): MapBoxPlatformGeocoder {
    val delegate = HttpApiPlatformGeocoder(
        forwardEndpoint = MapBoxForwardEndpoint(apiKey, parameters),
        reverseEndpoint = MapBoxReverseEndpoint(apiKey, parameters),
        json = json,
        client = client,
    )
    return object : HttpApiPlatformGeocoder by delegate, MapBoxPlatformGeocoder {}
}

/**
 * Creates a [MapBoxPlatformGeocoder] to be used with the [Geocoder].
 *
 * See [MapBox](https://www.mapbox.com/) for more information.
 *
 * @param apiKey The MapBox API key.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @param block A lambda that configures the [MapBoxParameters] to use for the geocoding requests.
 * @return A [MapBoxPlatformGeocoder] instance.
 */
public fun MapBoxPlatformGeocoder(
    apiKey: String,
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
    block: MapBoxParametersBuilder.() -> Unit,
): MapBoxPlatformGeocoder = MapBoxPlatformGeocoder(apiKey, mapBoxParameters(block), json, client)
