package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.web.mapbox.internal.GeocodeResponse
import dev.jordond.compass.geocoder.web.mapbox.internal.toPlaces
import dev.jordond.compass.geocoder.web.parameter.MapboxParameters
import dev.jordond.compass.geocoder.web.parameter.MapboxParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.mapboxParameters
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

/**
 * A [ReverseEndpoint] that uses the Mapbox Geocoding API.
 *
 * @property apiKey The API key to use for the Mapbox Geocoding API.
 * @property parameters The parameters to use for the Mapbox Geocoding API.
 */
public class MapboxReverseEndpoint(
    private val apiKey: String,
    private val parameters: MapboxParameters = MapboxParameters(),
) : ReverseEndpoint {

    /**
     * Creates a new [MapboxReverseEndpoint] with the given API key.
     *
     * @param apiKey The API key to use for the Mapbox Geocoding API.
     * @param block A block to configure the parameters for the Mapbox Geocoding API.
     */
    public constructor(
        apiKey: String,
        block: MapboxParametersBuilder.() -> Unit,
    ) : this(apiKey, mapboxParameters(block))

    override fun url(param: Coordinates): String {
        val (latitude, longitude) = param.run { latitude to longitude }
        return MapboxPlatformGeocoder.reverseUrl(latitude, longitude, apiKey, parameters)
    }

    override suspend fun mapResponse(response: HttpResponse): List<Place> {
        val forwardResponse = response.body<GeocodeResponse>()
        return forwardResponse.toPlaces()
    }
}