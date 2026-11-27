package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteResult
import dev.jordond.compass.autocomplete.GoogleMapsGeocoderAutocomplete
import dev.jordond.compass.geocoder.exception.GeocodeException
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsLocationType
import dev.jordond.compass.geocoder.web.parameter.googleMapsParameters
import dev.jordond.compass.test.MockHttp
import dev.jordond.compass.test.fixtures.GoogleMapsFixtures
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
class GoogleMapsAutocompleteServiceTest {

    private val http = MockHttp(body = GoogleMapsFixtures.geocodeJson)

    private fun service(apiKey: String = "secret", http: MockHttp = this.http) =
        GoogleMapsGeocoderAutocompleteService(apiKey = apiKey, client = http.client)

    @Test
    fun isAvailableRequiresANonBlankApiKey() {
        assertTrue(service("abc").isAvailable())
        assertFalse(service("").isAvailable())
        assertFalse(service("   ").isAvailable())
    }

    @Test
    fun searchSendsTheQueryAndKeyAndReturnsPlaces() = runTest {
        val result = service().search("1 Main St & Zürich")

        assertEquals(listOf(GoogleMapsFixtures.place), result)
        assertEquals("https://maps.googleapis.com/maps/api/geocode/json", http.urlWithoutQuery)
        assertEquals(mapOf("address" to "1 Main St & Zürich", "key" to "secret"), http.query)
    }

    @Test
    fun searchSendsTheConfiguredParameters() = runTest {
        val service = GoogleMapsGeocoderAutocompleteService(
            apiKey = "secret",
            parameters = googleMapsParameters { locationType(GoogleMapsLocationType.Rooftop) },
            client = http.client,
        )

        service.search("Paris")

        assertEquals(
            mapOf("address" to "Paris", "location_type" to "ROOFTOP", "key" to "secret"),
            http.query,
        )
    }

    @Test
    fun searchReturnsAnEmptyListForZeroResults() = runTest {
        val empty = MockHttp(body = """{"status":"ZERO_RESULTS","results":[]}""")

        assertEquals(emptyList(), service(http = empty).search("zzzzzz"))
    }

    @Test
    fun searchThrowsGeocodeExceptionForAnErrorStatus() = runTest {
        val denied = MockHttp(
            body = """{"status":"REQUEST_DENIED","error_message":"The provided API key is invalid.","results":[]}""",
        )

        val error = assertFailsWith<GeocodeException> { service(http = denied).search("Paris") }

        assertEquals(
            "Geocoding failed: [RequestDenied] The provided API key is invalid.",
            error.message,
        )
    }

    @Test
    fun searchThrowsWebExceptionForAnHttpError() = runTest {
        val failing = MockHttp(body = "{}", status = HttpStatusCode.Forbidden)

        val error = assertFailsWith<WebException> { service(http = failing).search("Paris") }

        assertEquals(HttpStatusCode.Forbidden, error.statusCode)
    }

    @Test
    fun autocompleteReturnsPlaces() = runTest {
        val autocomplete = GoogleMapsGeocoderAutocomplete(
            apiKey = "secret",
            client = http.client,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(
            AutocompleteResult.Success(listOf(GoogleMapsFixtures.place)),
            autocomplete.search("Mountain View"),
        )
    }

    @Test
    fun autocompleteFactoryReportsAnErrorStatusAsFailed() = runTest {
        val denied = MockHttp(body = """{"status":"REQUEST_DENIED","error_message":"bad key"}""")
        val autocomplete = Autocomplete(
            apiKey = "secret",
            client = denied.client,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(
            AutocompleteResult.Failed("Geocoding failed: [RequestDenied] bad key"),
            autocomplete.search("Mountain View"),
        )
    }
}
