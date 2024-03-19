package dev.jordond.compass.geocoder.web

import io.ktor.client.statement.HttpResponse

public interface HttpApiEndpoint<Params, Result> {

    public fun url(param: Params): String
    public fun mapResponse(response: HttpResponse): Result
}

public fun <Params, Result> HttpApiEndpoint(
    url: (Params) -> String,
    mapResponse: (HttpResponse) -> Result,
): HttpApiEndpoint<Params, Result> = object : HttpApiEndpoint<Params, Result> {
    override fun url(param: Params): String = url(param)
    override fun mapResponse(response: HttpResponse): Result = mapResponse(response)
}