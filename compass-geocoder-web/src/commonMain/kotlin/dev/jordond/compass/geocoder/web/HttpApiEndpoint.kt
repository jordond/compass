package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import io.ktor.client.statement.HttpResponse

/**
 * Alias that denotes a forward geocoding [HttpApiEndpoint].
 */
public typealias ForwardEndpoint = HttpApiEndpoint<String, List<Location>>

/**
 * Alias that denotes a reverse geocoding [HttpApiEndpoint].
 */
public typealias ReverseEndpoint = HttpApiEndpoint<Location, List<Place>>

/**
 * Defines an HTTP API endpoint.
 *
 * @param Params The type of the parameters to be passed to the endpoint.
 * @param Result The type of the result returned after mapping the response.
 */
public interface HttpApiEndpoint<Params, Result> {

    /**
     * Returns the URL for the endpoint with the given parameters.
     *
     * @param param The parameters to be passed to the endpoint.
     * @return The URL for the endpoint with the given parameters.
     */
    public fun url(param: Params): String

    /**
     * Maps the [HttpResponse] to the [Result] type.
     *
     * @param response The response to be mapped.
     * @return The result type.
     */
    public suspend fun mapResponse(response: HttpResponse): Result
}

/**
 * Creates an [HttpApiEndpoint] with the given URL and response mapping.
 *
 * @param Params The type of the parameters to be passed to the endpoint.
 * @param Result The type of the result returned after mapping the response.
 * @param url The URL for the endpoint.
 * @param mapResponse The response mapping.
 * @return The created [HttpApiEndpoint].
 */
public fun <Params, Result> HttpApiEndpoint(
    url: (Params) -> String,
    mapResponse: suspend (HttpResponse) -> Result,
): HttpApiEndpoint<Params, Result> = object : HttpApiEndpoint<Params, Result> {
    override fun url(param: Params): String = url(param)
    override suspend fun mapResponse(response: HttpResponse): Result = mapResponse(response)
}