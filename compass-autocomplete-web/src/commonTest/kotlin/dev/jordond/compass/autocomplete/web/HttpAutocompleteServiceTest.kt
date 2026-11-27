package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteOptions
import dev.jordond.compass.autocomplete.AutocompleteResult
import dev.jordond.compass.autocomplete.AutocompleteService
import dev.jordond.compass.tools.web.HttpApiEndpoint
import dev.jordond.compass.tools.web.exception.WebException
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HttpAutocompleteServiceTest {

    private val suggestionsJson = """["Paris, France","Paris, Texas"]"""

    private val endpoint: SearchEndpoint<String> = HttpApiEndpoint(
        url = { query -> "https://example.test/suggest?q=$query" },
        mapResponse = { response -> response.body<List<String>>() },
    )

    @Test
    fun isAvailableIsAlwaysTrue() {
        val service = HttpAutocompleteService(endpoint, client = mockClient { jsonResponse("[]") })

        assertTrue(service.isAvailable())
    }

    @Test
    fun searchRequestsUrlFromEndpointAndMapsResponse() = runTest {
        val urls = mutableListOf<String>()
        val client = mockClient { request ->
            urls += request.url.toString()
            jsonResponse(suggestionsJson)
        }
        val service = HttpAutocompleteService(endpoint, client = client)

        val result = service.search("paris")

        assertEquals(listOf("Paris, France", "Paris, Texas"), result)
        assertEquals(listOf("https://example.test/suggest?q=paris"), urls)
    }

    @Test
    fun searchReturnsEmptyListForEmptyResponse() = runTest {
        val service = HttpAutocompleteService(endpoint, client = mockClient { jsonResponse("[]") })

        assertEquals(emptyList(), service.search("nowhere"))
    }

    @Test
    fun searchMakesOneRequestPerCall() = runTest {
        var requests = 0
        val client = mockClient {
            requests++
            jsonResponse("[]")
        }
        val service = HttpAutocompleteService(endpoint, client = client)

        service.search("one")
        service.search("two")

        assertEquals(2, requests)
    }

    @Test
    fun searchThrowsWebExceptionForServerError() = runTest {
        val client = mockClient { jsonResponse("{}", HttpStatusCode.InternalServerError) }
        val service = HttpAutocompleteService(endpoint, client = client)

        val error = assertFailsWith<WebException> { service.search("paris") }

        assertEquals(HttpStatusCode.InternalServerError, error.statusCode)
        assertEquals("HTTP request failed with status code 500", error.message)
    }

    @Test
    fun searchThrowsWebExceptionForNotFound() = runTest {
        val client = mockClient { jsonResponse("{}", HttpStatusCode.NotFound) }
        val service = HttpAutocompleteService(endpoint, client = client)

        val error = assertFailsWith<WebException> { service.search("paris") }

        assertEquals(HttpStatusCode.NotFound, error.statusCode)
    }

    @Test
    fun searchPropagatesMalformedPayload() = runTest {
        val client = mockClient { jsonResponse("""{"not":"a list"}""") }
        val service = HttpAutocompleteService(endpoint, client = client)

        assertFailsWith<Exception> { service.search("paris") }
    }

    @Test
    fun autocompleteOverServiceMapsSuccess() = runTest {
        val service: AutocompleteService<String> =
            HttpAutocompleteService(endpoint, client = mockClient { jsonResponse(suggestionsJson) })
        val autocomplete = Autocomplete(
            service = service,
            options = AutocompleteOptions(minimumQuery = 2),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val result = autocomplete.search("paris")

        assertEquals(AutocompleteResult.Success(listOf("Paris, France", "Paris, Texas")), result)
    }

    @Test
    fun autocompleteOverServiceMapsHttpErrorToFailed() = runTest {
        val service: AutocompleteService<String> = HttpAutocompleteService(
            endpoint,
            client = mockClient { jsonResponse("{}", HttpStatusCode.BadGateway) },
        )
        val autocomplete = Autocomplete(
            service = service,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val result = autocomplete.search("paris")

        assertEquals(AutocompleteResult.Failed("HTTP request failed with status code 502"), result)
    }

    @Test
    fun autocompleteFromHttpServiceSearches() = runTest {
        val service = HttpAutocompleteService(endpoint, client = mockClient { jsonResponse(suggestionsJson) })

        val autocomplete = Autocomplete(
            httpService = service,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(
            AutocompleteResult.Success(listOf("Paris, France", "Paris, Texas")),
            autocomplete.search("paris"),
        )
    }

    @Test
    fun autocompleteFromEndpointSearches() = runTest {
        val autocomplete = Autocomplete(
            endpoint = endpoint,
            httpClient = mockClient { jsonResponse(suggestionsJson) },
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(
            AutocompleteResult.Success(listOf("Paris, France", "Paris, Texas")),
            autocomplete.search("paris"),
        )
    }
}
