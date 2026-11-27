package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.AutocompleteOptions
import dev.jordond.compass.autocomplete.AutocompleteResult
import dev.jordond.compass.autocomplete.MapboxGeocoderAutocomplete
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MapboxAutocompleteServiceTest {

    private val okJson = """
        {
          "type": "FeatureCollection",
          "features": [
            {
              "properties": {
                "name": "Kitchener",
                "coordinates": {"latitude": 43.4516, "longitude": -80.4925},
                "context": {
                  "place": {"name": "Kitchener"},
                  "district": {"name": "Waterloo Region"},
                  "region": {"name": "Ontario", "region_code": "ON"},
                  "postcode": {"name": "N2G"},
                  "country": {"name": "Canada", "country_code": "CA"}
                }
              }
            },
            {
              "properties": {
                "name": "No coordinates",
                "context": {"place": {"name": "Nowhere"}}
              }
            }
          ]
        }
    """.trimIndent()

    private val kitchener = Place(
        coordinates = Coordinates(43.4516, -80.4925),
        name = "Kitchener",
        street = null,
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
    ) = MapboxGeocoderAutocompleteService(apiKey = apiKey, client = mockClient(handler))

    @Test
    fun isAvailableRequiresNonBlankApiKey() {
        assertTrue(service("abc") { jsonResponse(okJson) }.isAvailable())
        assertFalse(service("") { jsonResponse(okJson) }.isAvailable())
        assertFalse(service("  ") { jsonResponse(okJson) }.isAvailable())
    }

    @Test
    fun searchMapsFeaturesToPlacesAndDropsEntriesWithoutCoordinates() = runTest {
        val result = service { jsonResponse(okJson) }.search("Kitchener")

        assertEquals(listOf(kitchener), result)
    }

    @Test
    fun searchBuildsForwardGeocodeRequestUrl() = runTest {
        val urls = mutableListOf<String>()
        val service = service("secret") { request ->
            urls += request.url.toString()
            jsonResponse(okJson)
        }

        service.search("Kitchener")

        val url = urls.single()
        assertTrue(url.startsWith("https://api.mapbox.com/search/geocode/v6/forward?q=Kitchener"), url)
        assertTrue(url.endsWith("&access_token=secret"), url)
    }

    @Test
    fun searchSendsQueryWithSpacesAsSingleParameter() = runTest {
        val urls = mutableListOf<String>()
        val service = service { request ->
            urls += request.url.parameters["q"].orEmpty()
            jsonResponse(okJson)
        }

        service.search("12 Main St, Kitchener")

        assertEquals(listOf("12 Main St, Kitchener"), urls)
    }

    @Test
    fun searchReturnsEmptyListForNoFeatures() = runTest {
        val result = service { jsonResponse("""{"features":[]}""") }.search("zzzzzz")

        assertEquals(emptyList(), result)
    }

    @Test
    fun searchThrowsWebExceptionForHttpError() = runTest {
        val service = service { jsonResponse("{}", HttpStatusCode.Unauthorized) }

        val error = assertFailsWith<WebException> { service.search("Kitchener") }

        assertEquals(HttpStatusCode.Unauthorized, error.statusCode)
    }

    @Test
    fun searchThrowsForMalformedPayload() = runTest {
        assertFailsWith<Throwable> { service { jsonResponse("""{"unexpected":true}""") }.search("Kit") }
    }

    @Test
    fun autocompleteReturnsPlaces() = runTest {
        val autocomplete = MapboxGeocoderAutocomplete(
            apiKey = "test-key",
            client = mockClient { jsonResponse(okJson) },
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(AutocompleteResult.Success(listOf(kitchener)), autocomplete.search("Kitchener"))
    }

    @Test
    fun autocompleteViaFactoryReturnsPlaces() = runTest {
        val autocomplete = Autocomplete(
            apiKey = "test-key",
            client = mockClient { jsonResponse(okJson) },
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(AutocompleteResult.Success(listOf(kitchener)), autocomplete.search("Kitchener"))
    }

    @Test
    fun autocompleteWithBlankKeyIsNotSupportedAndMakesNoRequest() = runTest {
        var requests = 0
        val autocomplete = MapboxGeocoderAutocomplete(
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
        val autocomplete = MapboxGeocoderAutocomplete(
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
        val autocomplete = MapboxGeocoderAutocomplete(
            apiKey = "test-key",
            client = mockClient { jsonResponse("{}", HttpStatusCode.TooManyRequests) },
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        assertEquals(
            AutocompleteResult.Failed("HTTP request failed with status code 429"),
            autocomplete.search("Kitchener"),
        )
    }
}
