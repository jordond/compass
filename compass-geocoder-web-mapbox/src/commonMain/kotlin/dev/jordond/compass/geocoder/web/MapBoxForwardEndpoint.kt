package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Location
import dev.jordond.compass.geocoder.web.internal.GeocodeResponse
import dev.jordond.compass.geocoder.web.internal.toLocations
import dev.jordond.compass.geocoder.web.parameters.MapBoxParameters
import dev.jordond.compass.geocoder.web.parameters.MapBoxParametersBuilder
import dev.jordond.compass.geocoder.web.parameters.mapBoxParameters
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.encodeURLQueryComponent

/**
 * A [ForwardEndpoint] that uses the MapBox Geocoding API.
 *
 * @property apiKey The API key to use for the MapBox Geocoding API.
 * @property parameters The parameters to use for the MapBox Geocoding API.
 */
public class MapBoxForwardEndpoint(
    private val apiKey: String,
    private val parameters: MapBoxParameters = MapBoxParameters(),
) : ForwardEndpoint {

    /**
     * Creates a new [MapBoxForwardEndpoint] with the given API key.
     *
     * @param apiKey The API key to use for the MapBox Geocoding API.
     * @param block A block to configure the parameters for the MapBox Geocoding API.
     */
    public constructor(
        apiKey: String,
        block: MapBoxParametersBuilder.() -> Unit,
    ) : this(apiKey, mapBoxParameters(block))

    override fun url(param: String): String {
        val encodedQuery = param.encodeURLQueryComponent()
        return MapBoxPlatformGeocoder.forwardUrl(encodedQuery, apiKey, parameters)
    }

    override suspend fun mapResponse(response: HttpResponse): List<Location> {
        val forwardResponse = response.body<GeocodeResponse>()
        return forwardResponse.toLocations()
    }
}