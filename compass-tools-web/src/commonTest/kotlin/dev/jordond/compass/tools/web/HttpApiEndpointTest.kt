package dev.jordond.compass.tools.web

import dev.jordond.compass.test.MockHttp
import dev.jordond.compass.test.jsonResponse
import dev.jordond.compass.tools.web.exception.WebException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Covers the client [HttpApiEndpoint.httpClient] builds, which is what a consumer gets by default.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HttpApiEndpointTest {

    private val url = "https://example.test"

    @Test
    fun defaultClientRetriesServerErrorsThreeTimesBeforeFailing() = runTest {
        val http = MockHttp(
            body = "{}",
            status = HttpStatusCode.InternalServerError,
            enableRetry = true,
        )

        val exception = assertFailsWith<WebException> {
            http.client.makeRequest(url) { "unused" }
        }

        assertEquals(HttpStatusCode.InternalServerError, exception.statusCode)
        assertEquals(4, http.requests.size)
        // The backoff ran on the test scheduler, so the test did not really wait
        assertTrue(currentTime > 0)
    }

    @Test
    fun defaultClientReturnsTheResponseOnceARetrySucceeds() = runTest {
        var calls = 0
        val http = MockHttp(enableRetry = true) {
            calls++
            if (calls == 1) {
                jsonResponse("{}", HttpStatusCode.BadGateway)
            } else {
                jsonResponse("recovered")
            }
        }

        val result = http.client.makeRequest(url) { response -> response.bodyAsText() }

        assertEquals("recovered", result)
        assertEquals(2, http.requests.size)
    }

    @Test
    fun defaultClientDoesNotRetryClientErrors() = runTest {
        val http = MockHttp(body = "{}", status = HttpStatusCode.NotFound, enableRetry = true)

        val exception = assertFailsWith<WebException> {
            http.client.makeRequest(url) { "unused" }
        }

        assertEquals(HttpStatusCode.NotFound, exception.statusCode)
        assertEquals(1, http.requests.size)
    }

    @Test
    fun factoryBuildsAnEndpointFromTheGivenFunctions() = runTest {
        val endpoint = HttpApiEndpoint<String, Int>(
            url = { path -> "$url/$path" },
            mapResponse = { response -> response.bodyAsText().length },
        )
        val http = MockHttp(body = "abcd")

        val result = http.client.makeRequest(endpoint.url("foo"), endpoint::mapResponse)

        assertEquals(4, result)
        assertEquals("$url/foo", http.url.toString())
    }
}
