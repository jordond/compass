package dev.jordond.compass.geocoder

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OpenCageGeocoderTest {

    private fun TestScope.geocoder(http: MockHttp): Geocoder = OpenCageGeocoder(
        apiKey = "secret",
        client = http.client,
        dispatcher = UnconfinedTestDispatcher(testScheduler),
    )

    @Test
    fun forwardReturnsCoordinates() = runTest {
        val http = MockHttp(body = OK_BODY)

        val result = geocoder(http).forward("Berlin")

        assertEquals(
            listOf(Coordinates(52.52, 13.405)),
            assertIs<GeocoderResult.Success<Coordinates>>(result).data,
        )
        val request = http.requests.single()
        assertEquals("api.opencagedata.com", request.url.host)
        assertEquals("/geocode/v1/json", request.url.encodedPath)
        assertEquals("secret", request.url.parameters["key"])
    }

    @Test
    fun reverseReturnsPlaces() = runTest {
        val http = MockHttp(body = OK_BODY)

        val result = geocoder(http).reverse(52.52, 13.405)

        val place = assertIs<GeocoderResult.Success<Place>>(result).data.single()
        assertEquals("Germany", place.country)
        assertEquals("Berlin", place.locality)
        val request = http.requests.single()
        assertEquals("52.52 13.405", request.url.parameters["q"])
        assertEquals("secret", request.url.parameters["key"])
    }

    @Test
    fun emptyResultsIsNotFound() = runTest {
        val http = MockHttp(body = """{"results":[],"status":{"code":200,"message":"OK"}}""")

        assertEquals(GeocoderResult.NotFound, geocoder(http).forward("nowhere"))
        assertEquals(GeocoderResult.NotFound, geocoder(http).reverse(0.0, 0.0))
    }

    @Test
    fun placesWithoutUsableComponentsAreNotFoundOnReverse() = runTest {
        val http = MockHttp(
            body = """{"results":[{"components":{},"formatted":"x","geometry":{"lat":1.0,"lng":2.0}}]}""",
        )

        assertEquals(GeocoderResult.NotFound, geocoder(http).reverse(1.0, 2.0))
    }

    @Test
    fun invalidKeyResponseIsGeocodeFailed() = runTest {
        val http = MockHttp(
            status = HttpStatusCode.Unauthorized,
            body = """{"status":{"code":401,"message":"invalid API key"}}""",
        )

        assertIs<GeocoderResult.GeocodeFailed>(geocoder(http).forward("x"))
    }

    @Test
    fun rateLimitWithoutExpectSuccessReportsStatusCode() = runTest {
        val http = MockHttp(
            status = HttpStatusCode.TooManyRequests,
            body = "{}",
            expectSuccess = false,
        )

        val failed = assertIs<GeocoderResult.GeocodeFailed>(geocoder(http).forward("x"))

        assertEquals("HTTP request failed with status code 429", failed.message)
    }

    @Test
    fun malformedBodyIsGeocodeFailed() = runTest {
        assertIs<GeocoderResult.GeocodeFailed>(geocoder(MockHttp(body = "nope")).reverse(1.0, 2.0))
    }

    @Test
    fun parametersAreSentWithTheRequest() = runTest {
        val http = MockHttp(body = OK_BODY)
        val geocoder = OpenCageGeocoder(
            apiKey = "secret",
            client = http.client,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        ) {
            countryCode("de")
        }

        geocoder.forward("x")

        assertTrue(http.requests.single().url.toString().contains("countrycode"))
    }

    private companion object {

        const val OK_BODY = """
            {"results":[{
              "components":{"_normalized_city":"Berlin","country":"Germany","country_code":"de","road":"Friedrichstrasse"},
              "formatted":"Friedrichstrasse, Berlin, Germany",
              "geometry":{"lat":52.52,"lng":13.405}
            }],"status":{"code":200,"message":"OK"}}
        """
    }
}
