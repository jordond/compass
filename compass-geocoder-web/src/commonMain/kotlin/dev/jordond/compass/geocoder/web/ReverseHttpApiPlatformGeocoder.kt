package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.exception.NotSupportedException
import dev.jordond.compass.geocoder.web.internal.makeRequest
import io.ktor.client.HttpClient

/**
 * A [HttpApiPlatformGeocoder] for reverse geocoding that uses the provided [ReverseEndpoint].
 *
 * @see HttpApiPlatformGeocoder
 * @property endpoint The endpoint to use for reverse geocoding.
 * @property client The [HttpClient] to use for making requests.
 */
public class ReverseHttpApiPlatformGeocoder(
    private val endpoint: ReverseEndpoint,
    private val client: HttpClient,
) : HttpApiPlatformGeocoder {

    override fun isAvailable(): Boolean = true

    override suspend fun forward(address: String): List<Coordinates> {
        throw NotSupportedException()
    }

    override suspend fun reverse(latitude: Double, longitude: Double): List<Place> {
        val url = endpoint.url(Coordinates(latitude, longitude))
        return client.makeRequest(url, endpoint::mapResponse)
    }
}