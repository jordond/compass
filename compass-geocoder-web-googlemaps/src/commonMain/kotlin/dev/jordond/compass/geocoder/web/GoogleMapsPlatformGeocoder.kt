package dev.jordond.compass.geocoder.web

import dev.jordond.compass.geocoder.web.parameter.GoogleMapsParameters
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.googleMapsParameters
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

/**
 * Defines a [HttpApiPlatformGeocoder] that uses the Google Maps Geocoding API.
 *
 * See [Google Maps](https://developers.google.com/maps/documentation/geocoding)
 * for more information.
 */
public interface GoogleMapsPlatformGeocoder : HttpApiPlatformGeocoder {

    public companion object {

        private const val BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json"

        private fun createUrl(
            target: String,
            apiKey: String,
            params: GoogleMapsParameters,
        ): String = "$BASE_URL?$target&${params.encode()}&key=$apiKey"

        internal fun forwardUrl(address: String, apiKey: String, params: GoogleMapsParameters) =
            createUrl(target = "address=$address", apiKey = apiKey, params = params)

        internal fun reverseUrl(
            latitude: Double,
            longitude: Double,
            apiKey: String,
            params: GoogleMapsParameters,
        ) = createUrl(target = "latlng=$latitude,$longitude", apiKey = apiKey, params = params)
    }
}

/**
 * Creates a [GoogleMapsPlatformGeocoder] to be used with the [Geocoder].
 *
 * See [Google Maps](https://developers.google.com/maps/documentation/geocoding) for more information.
 *
 * @param apiKey The Google Maps API key.
 * @param parameters The parameters to use for the geocoding requests.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @return A [GoogleMapsPlatformGeocoder] instance.
 */
public fun GoogleMapsPlatformGeocoder(
    apiKey: String,
    parameters: GoogleMapsParameters = GoogleMapsParameters(),
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
): GoogleMapsPlatformGeocoder {
    val delegate = HttpApiPlatformGeocoder(
        forwardEndpoint = GoogleMapsForwardEndpoint(apiKey, parameters),
        reverseEndpoint = GoogleMapsReverseEndpoint(apiKey, parameters),
        json = json,
        client = client,
    )
    return object : HttpApiPlatformGeocoder by delegate, GoogleMapsPlatformGeocoder {}
}

/**
 * Creates a [GoogleMapsPlatformGeocoder] to be used with the [Geocoder].
 *
 * See [Google Maps](https://developers.google.com/maps/documentation/geocoding) for more information.
 *
 * @param apiKey The Google Maps API key.
 * @param json The [Json] instance to use for serialization and deserialization.
 * @param client The [HttpClient] to use for making requests.
 * @param block A lambda that configures the [GoogleMapsParameters] to use for the geocoding requests.
 * @return A [GoogleMapsPlatformGeocoder] instance.
 */
public fun GoogleMapsPlatformGeocoder(
    apiKey: String,
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
    block: GoogleMapsParametersBuilder.() -> Unit,
): GoogleMapsPlatformGeocoder =
    GoogleMapsPlatformGeocoder(apiKey, googleMapsParameters(block), json, client)
