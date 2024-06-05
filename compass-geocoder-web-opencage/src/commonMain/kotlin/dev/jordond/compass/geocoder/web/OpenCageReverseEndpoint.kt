package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.web.opencage.internal.GeocodeResponse
import dev.jordond.compass.geocoder.web.opencage.internal.toPlaces
import dev.jordond.compass.geocoder.web.parameter.OpenCageParameters
import dev.jordond.compass.geocoder.web.parameter.OpenCageParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.openCageParameters
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

/**
 * A [ReverseEndpoint] that uses the OpenCage Geocoding API.
 *
 * @property apiKey The API key to use for the OpenCage Geocoding API.
 * @property parameters The parameters to use for the OpenCage Geocoding API.
 */
public class OpenCageReverseEndpoint(
    private val apiKey: String,
    private val parameters: OpenCageParameters = OpenCageParameters(),
) : ReverseEndpoint {

    /**
     * Creates a new [OpenCageReverseEndpoint] with the given API key.
     *
     * @param apiKey The API key to use for the OpenCage Geocoding API.
     * @param block A block to configure the parameters for the OpenCage Geocoding API.
     */
    public constructor(
        apiKey: String,
        block: OpenCageParametersBuilder.() -> Unit,
    ) : this(apiKey, openCageParameters(block))

    override fun url(param: Coordinates): String {
        val (latitude, longitude) = param.run { latitude to longitude }
        return OpenCagePlatformGeocoder.reverseUrl(latitude, longitude, apiKey, parameters)
    }

    override suspend fun mapResponse(response: HttpResponse): List<Place> {
        val result = response.body<GeocodeResponse>()
        return result.toPlaces()
    }
}