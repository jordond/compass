package dev.jordond.compass.tools.web

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HttpApiEndpointTest {

    @Test
    fun factoryDelegatesUrlAndMapResponse() = runTest {
        val endpoint = HttpApiEndpoint<String, Int>(
            url = { "https://example.com/$it" },
            mapResponse = { it.bodyAsText().length },
        )
        val client = io.ktor.client.HttpClient(MockEngine { respond("abcd", HttpStatusCode.OK) })

        assertEquals("https://example.com/foo", endpoint.url("foo"))
        assertEquals(4, endpoint.mapResponse(client.get("https://example.com")))
    }

    @Test
    fun defaultJsonIgnoresUnknownKeys() {
        assertTrue(HttpApiEndpoint.json().configuration.ignoreUnknownKeys)
    }

    @Test
    fun jsonBlockCanOverrideDefaults() {
        val json = HttpApiEndpoint.json { ignoreUnknownKeys = false; isLenient = true }
        assertFalse(json.configuration.ignoreUnknownKeys)
        assertTrue(json.configuration.isLenient)
    }

    @Test
    fun httpClientBlockIsApplied() {
        var applied = false
        HttpApiEndpoint.httpClient(enableRetry = false) { applied = true }.close()
        assertEquals(true, applied)
    }
}
