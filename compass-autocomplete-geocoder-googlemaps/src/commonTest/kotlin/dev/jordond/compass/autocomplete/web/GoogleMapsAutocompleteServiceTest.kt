package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteOptions
import dev.jordond.compass.autocomplete.AutocompleteResult
import dev.jordond.compass.autocomplete.GoogleMapsGeocoderAutocomplete
import dev.jordond.compass.tools.web.exception.WebException
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GoogleMapsAutocompleteServiceTest {

    private val okJson = """
        {
          "status": "OK",
          "results": [
            {
              "formatted_address": "Kitchener, ON, Canada",
              "address_components": [
                {"long_name": "Kitchener", "short_name": "Kitchener", "types": ["locality", "political"]},
                {"long_name": "Waterloo Region", "short_name": "Waterloo Region", "types": ["administrative_area_level_2"]},
                {"long_name": "Ontario", "short_name": "ON", "types": ["administrative_area_level_1"]},
                {"long_name": "Canada", "short_name": "CA", "types": ["country", "political"]},
                {"long_name": "N2G", "short_name": "N2G", "types": ["postal_code"]}
              ],
              "geometry": {"location": {"lat": 43.4516, "lng": -80.4925}}
            },
            {
              "formatted_address": "No geometry",
              "address_components": []
            }
          ]
        }
    """.trimIndent()

    private val kitchener = Place(
        coordinates = Coordinates(43.4516, -80.4925),
        name = null,
        street = "Kitchener, ON, Canada",
        isoCountryCode = "CA",
        country = "Canada",
        postalCode = "N2G",
        administrativeArea = "Ontario",
        subAdministrativeArea = "Waterloo Region",
        locality = "Kitchener",
        subLocality = null,
        thoroughfare = null,
        subThoroughfare = null,
    )

    private fun service(
        apiKey: String = "test-key",
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ) = GoogleMapsGeocoderAutocompleteService(apiKey = apiKey, client = mockClient(handler))

    @Test
    fun isAvailableRequiresNonBlankApiKey() {
        assertTrue(service("abc") { jsonResponse(okJson) }.isAvailable())
        assertFalse(service("") { jsonResponse(okJson) }.isAvailable())
        assertFalse(service("   ") { jsonResponse(okJson) }.isAvailable())
    }

    @Test
    fun searchMapsResultsToPlacesAndDropsEntriesWithoutGeometry() = runTest {
        val result = service { jsonResponse(okJson) }.search("Kitchener")

        assertEquals(listOf(kitchener), result)
    }

    @Test
    fun searchBuildsGeocodeRequestUrl() = runTest {
        val urls = mutableListOf<String>()
        val service = service("secret") { request ->
            urls += request.url.toString()
            jsonResponse(okJson)
        }

        service.search("Kitchener")

        val url = urls.single()
        assertTrue(url.startsWith("https://maps.googleapis.com/maps/api/geocode/json?address=Kitchener"), url)
        assertTrue(url.endsWith("&key=secret"), url)
    }

    @Test
    fun searchSendsQueryWithSpacesAsSingleParameter() = runTest {
        val urls = mutableListOf<String>()
        val service = service { request ->
            urls += request.url.parameters["address"].orEmpty()
            jsonResponse(okJson)
        }

        service.search("12 Main St, Kitchener")

        assertEquals(listOf("12 Main St, Kitchener"), urls)
    }

    @Test
    fun searchReturnsEmptyListForZeroResults() = runTest {
        val result = service { jsonResponse("""{"status":"ZERO_RESULTS","results":[]}""") }
            .search("zzzzzz")

        assertEquals(emptyList(), result)
    }

    @Test
    fun searchThrowsForRequestDeniedStatus() = runTest {
        val body = """{"status":"REQUEST_DENIED","error_message":"The provided API key is invalid.","results":[]}"""
        val service = service { jsonResponse(body) }

        val error = assertFailsWith<Throwable> { service.search("Kitchener") }

        assertTrue("The provided API key is invalid." in error.message.orEmpty(), error.message)
    }

    @Test
    fun searchThrowsForOverQueryLimitStatus() = runTest {
        val body = """{"status":"OVER_QUERY_LIMIT","results":[]}"""

        assertFailsWith<Throwable> { service { jsonResponse(body) }.search("Kitchener") }
    }

    @Test
    fun searchThrowsWebExceptionForHttpError() = runTest {
        val service = service { jsonResponse("{}", HttpStatusCode.Forbidden) }

        val error = assertFailsWith<WebException> { service.search("Kitchener") }

        assertEquals(HttpStatusCode.Forbidden, error.statusCode)
    }

    @Test
    fun searchThrowsForMalformedPayload() = runTest {
        assertFailsWith<Throwable> { service { jsonResponse("not json") }.search("Kitchener") }
    }

    @Test
    fun autocompleteReturnsPlaces() = runTest {
        val autocomplete = GoogleMapsGeocoderAutocomplete(
            apiKey = "test-key",
            client = mockClient { jsonResponse(okJson) },
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(AutocompleteResult.Success(listOf(kitchener)), autocomplete.search("Kitchener"))
    }

    @Test
    fun autocompleteMapsApiStatusErrorToFailed() = runTest {
        val body = """{"status":"REQUEST_DENIED","error_message":"bad key","results":[]}"""
        val autocomplete = Autocomplete(
            apiKey = "test-key",
            client = mockClient { jsonResponse(body) },
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val result = autocomplete.search("Kitchener")

        assertIs<AutocompleteResult.Failed>(result)
        assertTrue("bad key" in result.message, result.message)
    }

    @Test
    fun autocompleteWithBlankKeyIsNotSupportedAndMakesNoRequest() = runTest {
        var requests = 0
        val autocomplete = GoogleMapsGeocoderAutocomplete(
            apiKey = "",
            client = mockClient {
                requests++
                jsonResponse(okJson)
            },
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(AutocompleteResult.NotSupported, autocomplete.search("Kitchener"))
        assertEquals(0, requests)
    }

    @Test
    fun autocompleteHonoursMinimumQuery() = runTest {
        var requests = 0
        val autocomplete = GoogleMapsGeocoderAutocomplete(
            apiKey = "test-key",
            options = AutocompleteOptions(minimumQuery = 6),
            client = mockClient {
                requests++
                jsonResponse(okJson)
            },
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(AutocompleteResult.Success(emptyList<Place>()), autocomplete.search("Kit"))
        assertEquals(0, requests)
    }

    @Test
    fun autocompleteMapsHttpErrorToFailed() = runTest {
        val autocomplete = GoogleMapsGeocoderAutocomplete(
            apiKey = "test-key",
            client = mockClient { jsonResponse("{}", HttpStatusCode.ServiceUnavailable) },
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(
            AutocompleteResult.Failed("HTTP request failed with status code 503"),
            autocomplete.search("Kitchener"),
        )
    }
}
