package dev.jordond.compass.geocoder.web.internal

import dev.jordond.compass.geocoder.exception.GeocodeException
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CancellationException

internal suspend fun <Result> HttpClient.makeRequest(
    url: String,
    resultMapper: (HttpResponse) -> Result,
): Result {
    try {
        val response = get { url(url) }
        return resultMapper(response)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (cause: Throwable) {
        throw GeocodeException(cause.message ?: "Unable to make http request")
    }
}