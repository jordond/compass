package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteResult
import dev.jordond.compass.test.MockHttp
import dev.jordond.compass.tools.web.HttpApiEndpoint
import dev.jordond.compass.tools.web.exception.WebException
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class HttpAutocompleteServiceTest {

    private val suggestions = listOf("Paris, France", "Paris, Texas")

    private val http = MockHttp(body = """["Paris, France","Paris, Texas"]""")

    private val endpoint: SearchEndpoint<String> = HttpApiEndpoint(
        url = { query -> "https://example.test/suggest?q=$query" },
        mapResponse = { response -> response.body<List<String>>() },
    )

    @Test
    fun searchRequestsTheEndpointUrlAndReturnsItsMapping() = runTest {
        val result = HttpAutocompleteService(endpoint, client = http.client).search("paris")

        assertEquals(suggestions, result)
        assertEquals("https://example.test/suggest?q=paris", http.url.toString())
    }

    @Test
    fun searchPassesTheRawQueryToTheEndpoint() = runTest {
        val queries = mutableListOf<String>()
        val recording: SearchEndpoint<String> = HttpApiEndpoint(
            url = { query ->
                queries += query
                "https://example.test/suggest"
            },
            mapResponse = { emptyList() },
        )

        HttpAutocompleteService(recording, client = http.client).search("1 Main St & Co")

        assertEquals(listOf("1 Main St & Co"), queries)
    }

    @Test
    fun searchThrowsWebExceptionForAnHttpError() = runTest {
        val failing = MockHttp(body = "{}", status = HttpStatusCode.InternalServerError)
        val service = HttpAutocompleteService(endpoint, client = failing.client)

        val error = assertFailsWith<WebException> { service.search("paris") }

        assertEquals(HttpStatusCode.InternalServerError, error.statusCode)
    }

    @Test
    fun searchFailsToConvertAPayloadOfTheWrongShape() = runTest {
        val malformed = MockHttp(body = """{"not":"a list"}""")
        val service = HttpAutocompleteService(endpoint, client = malformed.client)

        assertFailsWith<JsonConvertException> { service.search("paris") }
    }

    @Test
    fun autocompleteFromHttpServiceSearches() = runTest {
        val autocomplete = Autocomplete(
            httpService = HttpAutocompleteService(endpoint, client = http.client),
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(AutocompleteResult.Success(suggestions), autocomplete.search("paris"))
    }

    @Test
    fun autocompleteFromEndpointSearches() = runTest {
        val autocomplete = Autocomplete(
            endpoint = endpoint,
            httpClient = http.client,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(AutocompleteResult.Success(suggestions), autocomplete.search("paris"))
    }
}
