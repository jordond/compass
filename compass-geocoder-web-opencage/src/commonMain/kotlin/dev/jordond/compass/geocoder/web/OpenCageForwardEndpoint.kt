package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.web.parameter.OpenCageParameters
import dev.jordond.compass.geocoder.web.parameter.OpenCageParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.openCageParameters
import dev.jordond.compass.geocoder.web.template.internal.GeocodeResponse
import dev.jordond.compass.geocoder.web.template.internal.toCoordinates
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.encodeURLQueryComponent

/**
 * A [ForwardEndpoint] that uses the OpenCage Geocoding API.
 *
 * @property apiKey The API key to use for the OpenCage Geocoding API.
 * @property parameters The parameters to use for the OpenCage Geocoding API.
 */
public class OpenCageForwardEndpoint(
    private val apiKey: String,
    private val parameters: OpenCageParameters = OpenCageParameters(),
) : ForwardEndpoint {

    /**
     * Creates a new [OpenCageForwardEndpoint] with the given API key.
     *
     * @param apiKey The API key to use for the OpenCage Geocoding API.
     * @param block A block to configure the parameters for the OpenCage Geocoding API.
     */
    public constructor(
        apiKey: String,
        block: OpenCageParametersBuilder.() -> Unit,
    ) : this(apiKey, openCageParameters(block))

    override fun url(param: String): String {
        val encodedQuery = param.encodeURLQueryComponent()
        return OpenCageGeocoder.forwardUrl(encodedQuery, apiKey, parameters)
    }

    override suspend fun mapResponse(response: HttpResponse): List<Coordinates> {
        val result = response.body<GeocodeResponse>()
        return result.toCoordinates()
    }
}