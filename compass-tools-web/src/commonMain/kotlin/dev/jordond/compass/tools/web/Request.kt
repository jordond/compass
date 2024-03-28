package dev.jordond.compass.tools.web

import co.touchlab.kermit.Logger
import dev.jordond.compass.tools.web.exception.WebException
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CancellationException

public suspend fun <Result> HttpClient.makeRequest(
    url: String,
    resultMapper: suspend (HttpResponse) -> Result,
): Result {
    try {
        val response = get { url(url) }
        if (response.status.value in 200..299) {
            return resultMapper(response)
        } else {
            throw WebException(
                statusCode = response.status,
                message = "HTTP request failed with status code ${response.status.value}",
            )
        }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (webException: WebException) {
        throw webException
    } catch (cause: Throwable) {
        Logger.e(cause) { "Unable to make web request" }
        throw cause
    }
}