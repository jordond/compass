package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.web.google.internal.GeocodeResponse
import dev.jordond.compass.geocoder.web.google.internal.resultsOrThrow
import dev.jordond.compass.geocoder.web.google.internal.toCoordinates
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsParameters
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.googleMapsParameters
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.encodeURLQueryComponent

/**
 * A [ForwardEndpoint] that uses the Google Maps Geocoding API.
 *
 * @property apiKey The API key to use for the Google Maps Geocoding API.
 * @property parameters The parameters to use for the Google Maps Geocoding API.
 */
public class GoogleMapsForwardEndpoint(
    private val apiKey: String,
    private val parameters: GoogleMapsParameters = GoogleMapsParameters(),
) : ForwardEndpoint {

    /**
     * Creates a new [GoogleMapsForwardEndpoint] with the given API key.
     *
     * @param apiKey The API key to use for the Google Maps Geocoding API.
     * @param block A block to configure the parameters for the Google Maps Geocoding API.
     */
    public constructor(
        apiKey: String,
        block: GoogleMapsParametersBuilder.() -> Unit,
    ) : this(apiKey, googleMapsParameters(block))

    override fun url(param: String): String {
        val encodedQuery = param.encodeURLQueryComponent()
        return GoogleMapsPlatformGeocoder.forwardUrl(encodedQuery, apiKey, parameters)
    }

    override suspend fun mapResponse(response: HttpResponse): List<Coordinates> {
        val result = response.body<GeocodeResponse>().resultsOrThrow()
        return result.toCoordinates()
    }
}