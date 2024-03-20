package dev.jordond.compass.geocoder.web.internal

import co.touchlab.kermit.Logger
import dev.jordond.compass.geocoder.exception.GeocodeException
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CancellationException

internal suspend fun <Result> HttpClient.makeRequest(
    url: String,
    resultMapper: suspend (HttpResponse) -> Result,
): Result {
    try {
        val response = get { url(url) }
        if (response.status.value in 200..299) {
            return resultMapper(response)
        } else {
            throw GeocodeException("HTTP request failed with status code ${response.status.value}")
        }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (cause: GeocodeException) {
        throw cause
    } catch (cause: Throwable) {
        Logger.e(cause) { "Unable to make geocode request" }
        throw GeocodeException(cause.message ?: "Unable to make http request")
    }
}