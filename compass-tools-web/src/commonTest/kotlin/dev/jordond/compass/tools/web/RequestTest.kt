package dev.jordond.compass.tools.web

import dev.jordond.compass.test.MockHttp
import dev.jordond.compass.tools.web.exception.WebException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class RequestTest {

    private val url = "https://example.test/path?q=1"

    @Test
    fun successfulResponseIsPassedToMapper() = runTest {
        val http = MockHttp(body = "hello")

        val result = http.client.makeRequest(url) { response -> response.bodyAsText() }

        assertEquals("hello", result)
        assertEquals(url, http.url.toString())
    }

    @Test
    fun anyTwoHundredRangeStatusIsSuccess() = runTest {
        val http = MockHttp(body = "", status = HttpStatusCode.NoContent)

        val status = http.client.makeRequest(url) { response -> response.status }

        assertEquals(HttpStatusCode.NoContent, status)
    }

    @Test
    fun errorStatusThrowsWebExceptionWithStatusAndMessage() = runTest {
        val http = MockHttp(body = "{}", status = HttpStatusCode.BadRequest)

        val exception = assertFailsWith<WebException> {
            http.client.makeRequest(url) { "unused" }
        }

        assertEquals(HttpStatusCode.BadRequest, exception.statusCode)
        assertEquals("HTTP request failed with status code 400", exception.message)
    }

    @Test
    fun redirectStatusIsNotSuccess() = runTest {
        val http = MockHttp(body = "", status = HttpStatusCode.NotModified)

        val exception = assertFailsWith<WebException> {
            http.client.makeRequest(url) { "unused" }
        }

        assertEquals(HttpStatusCode.NotModified, exception.statusCode)
    }

    @Test
    fun clientThatDoesNotExpectSuccessThrowsTheSameWebException() = runTest {
        // A consumer can pass any client, this one leaves the status check to makeRequest
        val client = HttpClient(MockEngine { respondError(HttpStatusCode.Forbidden) })

        val exception = assertFailsWith<WebException> {
            client.makeRequest(url) { "unused" }
        }

        assertEquals(HttpStatusCode.Forbidden, exception.statusCode)
        assertEquals("HTTP request failed with status code 403", exception.message)
    }

    @Test
    fun mapperExceptionIsRethrownUnchanged() = runTest {
        val failure = IllegalStateException("bad body")
        val http = MockHttp(body = "x")

        val thrown = assertFailsWith<IllegalStateException> {
            http.client.makeRequest<String>(url) { throw failure }
        }

        assertSame(failure, thrown)
    }

    @Test
    fun engineFailureIsRethrown() = runTest {
        val http = MockHttp { throw IllegalStateException("no network") }

        val thrown = assertFailsWith<IllegalStateException> {
            http.client.makeRequest(url) { "unused" }
        }

        // Coroutines may hand back a copy of the exception, so compare the message
        assertEquals("no network", thrown.message)
    }

    @Test
    fun cancellationFromMapperIsRethrown() = runTest {
        val http = MockHttp(body = "x")

        assertFailsWith<CancellationException> {
            http.client.makeRequest<String>(url) { throw CancellationException("stop") }
        }
    }

    @Test
    fun cancellationFromEngineIsRethrown() = runTest {
        val http = MockHttp { throw CancellationException("stop") }

        assertFailsWith<CancellationException> {
            http.client.makeRequest(url) { "unused" }
        }
    }
}
