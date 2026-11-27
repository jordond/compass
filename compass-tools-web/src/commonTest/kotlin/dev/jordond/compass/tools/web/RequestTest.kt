package dev.jordond.compass.tools.web

import dev.jordond.compass.InternalCompassApi
import dev.jordond.compass.tools.web.exception.WebException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

@OptIn(InternalCompassApi::class)
class RequestTest {

    private fun client(handler: MockEngine) = HttpClient(handler)

    @Test
    fun successfulResponseIsPassedToMapper() = runTest {
        val urls = mutableListOf<String>()
        val engine = MockEngine { request ->
            urls += request.url.toString()
            respond("hello", HttpStatusCode.OK)
        }

        val result = client(engine).makeRequest("https://example.com/path?q=1") { it.bodyAsText() }

        assertEquals("hello", result)
        assertEquals(listOf("https://example.com/path?q=1"), urls)
    }

    @Test
    fun anyTwoHundredRangeStatusIsSuccess() = runTest {
        val engine = MockEngine { respond("", HttpStatusCode.NoContent) }
        val status = client(engine).makeRequest("https://example.com") { it.status }
        assertEquals(HttpStatusCode.NoContent, status)
    }

    @Test
    fun nonSuccessStatusThrowsWebExceptionWithStatus() = runTest {
        val engine = MockEngine { respondError(HttpStatusCode.BadRequest) }

        val exception = assertFailsWith<WebException> {
            client(engine).makeRequest("https://example.com") { "unused" }
        }

        assertEquals(HttpStatusCode.BadRequest, exception.statusCode)
        assertEquals("HTTP request failed with status code 400", exception.message)
    }

    @Test
    fun redirectStatusIsNotSuccess() = runTest {
        val engine = MockEngine { respond("", HttpStatusCode.NotModified) }
        val exception = assertFailsWith<WebException> {
            client(engine).makeRequest("https://example.com") { "unused" }
        }
        assertEquals(HttpStatusCode.NotModified, exception.statusCode)
    }

    @Test
    fun mapperExceptionIsRethrownUnchanged() = runTest {
        val failure = IllegalStateException("bad body")
        val engine = MockEngine { respond("x", HttpStatusCode.OK) }

        val thrown = assertFailsWith<IllegalStateException> {
            client(engine).makeRequest<String>("https://example.com") { throw failure }
        }

        assertSame(failure, thrown)
    }

    @Test
    fun engineFailureIsRethrown() = runTest {
        val failure = RuntimeException("no network")
        val engine = MockEngine { throw failure }

        val thrown = assertFailsWith<RuntimeException> {
            client(engine).makeRequest("https://example.com") { "unused" }
        }

        assertEquals("no network", thrown.message)
    }

    @Test
    fun cancellationFromMapperIsRethrown() = runTest {
        val engine = MockEngine { respond("x", HttpStatusCode.OK) }
        assertFailsWith<CancellationException> {
            client(engine).makeRequest<String>("https://example.com") {
                throw CancellationException("stop")
            }
        }
    }
}
