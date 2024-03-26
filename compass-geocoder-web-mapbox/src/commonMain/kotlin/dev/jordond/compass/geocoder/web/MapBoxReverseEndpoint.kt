package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.web.mapbox.internal.GeocodeResponse
import dev.jordond.compass.geocoder.web.mapbox.internal.toPlaces
import dev.jordond.compass.geocoder.web.parameter.MapBoxParameters
import dev.jordond.compass.geocoder.web.parameter.MapBoxParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.mapBoxParameters
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

/**
 * A [ReverseEndpoint] that uses the MapBox Geocoding API.
 *
 * @property apiKey The API key to use for the MapBox Geocoding API.
 * @property parameters The parameters to use for the MapBox Geocoding API.
 */
public class MapBoxReverseEndpoint(
    private val apiKey: String,
    private val parameters: MapBoxParameters = MapBoxParameters(),
) : ReverseEndpoint {

    /**
     * Creates a new [MapBoxReverseEndpoint] with the given API key.
     *
     * @param apiKey The API key to use for the MapBox Geocoding API.
     * @param block A block to configure the parameters for the MapBox Geocoding API.
     */
    public constructor(
        apiKey: String,
        block: MapBoxParametersBuilder.() -> Unit,
    ) : this(apiKey, mapBoxParameters(block))

    override fun url(param: Coordinates): String {
        val (latitude, longitude) = param.run { latitude to longitude }
        return MapBoxPlatformGeocoder.reverseUrl(latitude, longitude, apiKey, parameters)
    }

    override suspend fun mapResponse(response: HttpResponse): List<Place> {
        val forwardResponse = response.body<GeocodeResponse>()
        return forwardResponse.toPlaces()
    }
}