package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

/**
 * Alias that denotes a forward geocoding [HttpApiEndpoint].
 */
public typealias ForwardEndpoint = HttpApiEndpoint<String, List<Coordinates>>

/**
 * Alias that denotes a reverse geocoding [HttpApiEndpoint].
 */
public typealias ReverseEndpoint = HttpApiEndpoint<Coordinates, List<Place>>

/**
 * Defines the interface for a geocoder that uses an HTTP API to perform geocoding operations.
 */
public interface HttpApiPlatformGeocoder : PlatformGeocoder {

    public companion object
}

/**
 * Create a new [HttpApiPlatformGeocoder] with the given [forwardEndpoint] and [reverseEndpoint].
 *
 * Customization of the [Json] configuration and [HttpClient] can be done with
 * the [json] and [client] parameters.
 *
 * @param forwardEndpoint The endpoint to use for forward geocoding.
 * @param reverseEndpoint The endpoint to use for reverse geocoding.
 * @param json The [Json] configuration to use for the [HttpClient].
 * @param client The [HttpClient] to use for the geocoder.
 * @return A new [HttpApiPlatformGeocoder] with the given [forwardEndpoint] and [reverseEndpoint].
 */
public fun HttpApiPlatformGeocoder(
    forwardEndpoint: ForwardEndpoint,
    reverseEndpoint: ReverseEndpoint,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): HttpApiPlatformGeocoder = object : HttpApiPlatformGeocoder {

    override fun isAvailable(): Boolean = true

    override suspend fun forward(address: String): List<Coordinates> {
        return ForwardHttpApiPlatformGeocoder(forwardEndpoint, client)
            .forward(address)
    }

    override suspend fun reverse(latitude: Double, longitude: Double): List<Place> {
        return ReverseHttpApiPlatformGeocoder(reverseEndpoint, client)
            .reverse(latitude, longitude)
    }
}