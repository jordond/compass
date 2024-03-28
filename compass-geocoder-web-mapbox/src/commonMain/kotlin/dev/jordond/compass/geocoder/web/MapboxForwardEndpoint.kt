package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.web.mapbox.internal.GeocodeResponse
import dev.jordond.compass.geocoder.web.mapbox.internal.toCoordinates
import dev.jordond.compass.geocoder.web.parameter.MapboxParameters
import dev.jordond.compass.geocoder.web.parameter.MapboxParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.mapboxParameters
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.encodeURLQueryComponent

/**
 * A [ForwardEndpoint] that uses the Mapbox Geocoding API.
 *
 * @property apiKey The API key to use for the Mapbox Geocoding API.
 * @property parameters The parameters to use for the Mapbox Geocoding API.
 */
public class MapboxForwardEndpoint(
    private val apiKey: String,
    private val parameters: MapboxParameters = MapboxParameters(),
) : ForwardEndpoint {

    /**
     * Creates a new [MapboxForwardEndpoint] with the given API key.
     *
     * @param apiKey The API key to use for the Mapbox Geocoding API.
     * @param block A block to configure the parameters for the Mapbox Geocoding API.
     */
    public constructor(
        apiKey: String,
        block: MapboxParametersBuilder.() -> Unit,
    ) : this(apiKey, mapboxParameters(block))

    override fun url(param: String): String {
        val encodedQuery = param.encodeURLQueryComponent()
        return MapboxPlatformGeocoder.forwardUrl(encodedQuery, apiKey, parameters)
    }

    override suspend fun mapResponse(response: HttpResponse): List<Coordinates> {
        val forwardResponse = response.body<GeocodeResponse>()
        return forwardResponse.toCoordinates()
    }
}