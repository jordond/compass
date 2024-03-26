package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.web.parameter.TemplateParameters
import dev.jordond.compass.geocoder.web.parameter.TemplateParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.templateParameters
import dev.jordond.compass.geocoder.web.template.internal.GeocodeResponse
import dev.jordond.compass.geocoder.web.template.internal.toCoordinates
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.encodeURLQueryComponent

/**
 * A [ForwardEndpoint] that uses the TEMPLATE Geocoding API.
 *
 * @property apiKey The API key to use for the TEMPLATE Geocoding API.
 * @property parameters The parameters to use for the TEMPLATE Geocoding API.
 */
public class TemplateForwardEndpoint(
    private val apiKey: String,
    private val parameters: TemplateParameters = TemplateParameters(),
) : ForwardEndpoint {

    /**
     * Creates a new [TemplateForwardEndpoint] with the given API key.
     *
     * @param apiKey The API key to use for the TEMPLATE Geocoding API.
     * @param block A block to configure the parameters for the TEMPLATE Geocoding API.
     */
    public constructor(
        apiKey: String,
        block: TemplateParametersBuilder.() -> Unit,
    ) : this(apiKey, templateParameters(block))

    override fun url(param: String): String {
        val encodedQuery = param.encodeURLQueryComponent()
        return TemplatePlatformGeocoder.forwardUrl(encodedQuery, apiKey, parameters)
    }

    override suspend fun mapResponse(response: HttpResponse): List<Coordinates> {
        val result = response.body<GeocodeResponse>()
        return result.toCoordinates()
    }
}