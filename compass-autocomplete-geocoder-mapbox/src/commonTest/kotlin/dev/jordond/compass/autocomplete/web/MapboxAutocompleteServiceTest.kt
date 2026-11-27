package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteResult
import dev.jordond.compass.autocomplete.MapboxGeocoderAutocomplete
import dev.jordond.compass.geocoder.web.parameter.MapboxParameters
import dev.jordond.compass.test.MockHttp
import dev.jordond.compass.test.fixtures.MapboxFixtures
import dev.jordond.compass.tools.web.exception.WebException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MapboxAutocompleteServiceTest {

    private val http = MockHttp(body = MapboxFixtures.geocodeJson)

    private fun service(apiKey: String = "token", http: MockHttp = this.http) =
        MapboxGeocoderAutocompleteService(apiKey = apiKey, client = http.client)

    @Test
    fun isAvailableRequiresANonBlankApiKey() {
        assertTrue(service("abc").isAvailable())
        assertFalse(service("").isAvailable())
        assertFalse(service("  ").isAvailable())
    }

    @Test
    fun searchSendsTheQueryDefaultsAndTokenAndReturnsPlaces() = runTest {
        val result = service().search("1 Main St & Zürich")

        assertEquals(listOf(MapboxFixtures.place), result)
        assertEquals("https://api.mapbox.com/search/geocode/v6/forward", http.urlWithoutQuery)
        assertEquals(
            mapOf(
                "q" to "1 Main St & Zürich",
                "permanent" to "false",
                "autocomplete" to "true",
                "access_token" to "token",
            ),
            http.query,
        )
    }

    @Test
    fun searchSendsTheConfiguredParameters() = runTest {
        val service = MapboxGeocoderAutocompleteService(
            apiKey = "token",
            parameters = MapboxParameters(limit = 3, language = "fr"),
            client = http.client,
        )

        service.search("Paris")

        assertEquals(
            mapOf(
                "q" to "Paris",
                "permanent" to "false",
                "autocomplete" to "true",
                "limit" to "3",
                "language" to "fr",
                "access_token" to "token",
            ),
            http.query,
        )
    }

    @Test
    fun searchReturnsAnEmptyListForNoFeatures() = runTest {
        val empty = MockHttp(body = """{"features":[]}""")

        assertEquals(emptyList(), service(http = empty).search("zzzzzz"))
    }

    @Test
    fun searchThrowsWebExceptionForAnHttpError() = runTest {
        val failing = MockHttp(body = "{}", status = HttpStatusCode.Unauthorized)

        val error = assertFailsWith<WebException> { service(http = failing).search("Paris") }

        assertEquals(HttpStatusCode.Unauthorized, error.statusCode)
    }

    @Test
    fun autocompleteReturnsPlaces() = runTest {
        val autocomplete = MapboxGeocoderAutocomplete(
            apiKey = "token",
            client = http.client,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(
            AutocompleteResult.Success(listOf(MapboxFixtures.place)),
            autocomplete.search("Washington"),
        )
    }

    @Test
    fun autocompleteFactoryReturnsPlaces() = runTest {
        val autocomplete = Autocomplete(
            apiKey = "token",
            client = http.client,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(
            AutocompleteResult.Success(listOf(MapboxFixtures.place)),
            autocomplete.search("Washington"),
        )
    }
}
