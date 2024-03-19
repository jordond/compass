package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Location
import dev.jordond.compass.geocoder.web.parameter.QueryParameters
import dev.jordond.compass.geocoder.web.parameter.QueryParametersBuilder
import io.ktor.client.statement.HttpResponse


public interface HttpGeocoderApi<T : QueryParameters, B : QueryParametersBuilder<T>> {

    public val defaultParameters: T

    public suspend fun forward(query: String, params: T = defaultParameters): List<Location>

    public suspend fun forward(query: String, builder: B.() -> Unit = {}): List<Location>

    public suspend fun reverse(
        longitude: Double,
        latitude: Double,
        params: T = defaultParameters,
    ): List<Location>

    public suspend fun reverse(
        longitude: Double,
        latitude: Double,
        builder: B.() -> Unit = {},
    ): List<Location>
}