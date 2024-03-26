package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.web.parameter.TemplateParameters
import dev.jordond.compass.geocoder.web.parameter.TemplateParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.templateParameters
import dev.jordond.compass.geocoder.web.template.internal.GeocodeResponse
import dev.jordond.compass.geocoder.web.template.internal.toPlaces
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

/**
 * A [ReverseEndpoint] that uses the TEMPLATE Geocoding API.
 *
 * @property apiKey The API key to use for the TEMPLATE Geocoding API.
 * @property parameters The parameters to use for the TEMPLATE Geocoding API.
 */
public class TemplateReverseEndpoint(
    private val apiKey: String,
    private val parameters: TemplateParameters = TemplateParameters(),
) : ReverseEndpoint {

    /**
     * Creates a new [TemplateReverseEndpoint] with the given API key.
     *
     * @param apiKey The API key to use for the TEMPLATE Geocoding API.
     * @param block A block to configure the parameters for the TEMPLATE Geocoding API.
     */
    public constructor(
        apiKey: String,
        block: TemplateParametersBuilder.() -> Unit,
    ) : this(apiKey, templateParameters(block))

    override fun url(param: Coordinates): String {
        val (latitude, longitude) = param.run { latitude to longitude }
        return TemplatePlatformGeocoder.reverseUrl(latitude, longitude, apiKey, parameters)
    }

    override suspend fun mapResponse(response: HttpResponse): List<Place> {
        val result = response.body<GeocodeResponse>()
        return result.toPlaces()
    }
}