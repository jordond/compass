package dev.jordond.compass.geocoder.web.internal

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.web.HttpApiEndpoint
import dev.jordond.compass.geocoder.web.HttpApiPlatformGeocoder
import io.ktor.client.HttpClient

internal class DefaultHttpApiPlatformGeocoder(
    private val forwardEndpoint: HttpApiEndpoint<String, List<Location>>,
    private val reverseEndpoint: HttpApiEndpoint<Location, List<Place>>,
    private val client: HttpClient,
) : HttpApiPlatformGeocoder {

    override fun isAvailable(): Boolean = true

    override suspend fun locationFromAddress(address: String): List<Location> {
        return ForwardHttpApiPlatformGeocoder(forwardEndpoint, client)
            .locationFromAddress(address)
    }

    override suspend fun placeFromLocation(latitude: Double, longitude: Double): List<Place> {
        return ReverseHttpApiPlatformGeocoder(reverseEndpoint, client)
            .placeFromLocation(latitude, longitude)
    }
}