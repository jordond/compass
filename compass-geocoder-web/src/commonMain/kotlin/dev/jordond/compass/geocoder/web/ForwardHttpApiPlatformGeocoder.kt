package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.exception.NotSupportedException
import dev.jordond.compass.geocoder.web.internal.makeRequest
import io.ktor.client.HttpClient

/**
 * A [HttpApiPlatformGeocoder] for forward geocoding that uses the provided [ForwardEndpoint].
 *
 * @see HttpApiPlatformGeocoder
 * @property endpoint The endpoint to use for forward geocoding.
 * @property client The [HttpClient] to use for making requests.
 */
public class ForwardHttpApiPlatformGeocoder(
    private val endpoint: ForwardEndpoint,
    private val client: HttpClient,
) : HttpApiPlatformGeocoder {

    override fun isAvailable(): Boolean = true

    override suspend fun locationFromAddress(address: String): List<Location> {
        val url = endpoint.url(address)
        return client.makeRequest(url, endpoint::mapResponse)
    }

    override suspend fun placeFromLocation(latitude: Double, longitude: Double): List<Place> {
        throw NotSupportedException()
    }
}