package dev.jordond.compass.geocoder

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsLocationType
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsParametersBuilder
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GoogleMapsGeocoderTest {

    private fun TestScope.geocoder(
        http: MockHttp,
        block: GoogleMapsParametersBuilder.() -> Unit = {},
    ) =
        GoogleMapsGeocoder(
            apiKey = "secret",
            client = http.client,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
            block = block,
        )

    @Test
    fun forwardReturnsCoordinates() = runTest {
        val http = MockHttp(body = OK_BODY)

        val result = geocoder(http).forward("Mountain View")

        assertEquals(
            listOf(Coordinates(37.4224764, -122.0842499)),
            assertIs<GeocoderResult.Success<Coordinates>>(result).data,
        )
        val request = http.requests.single()
        assertEquals("maps.googleapis.com", request.url.host)
        assertEquals("/maps/api/geocode/json", request.url.encodedPath)
        assertEquals("secret", request.url.parameters["key"])
        assertTrue(request.url.parameters["address"].orEmpty().contains("Mountain"))
    }

    @Test
    fun reverseReturnsPlaces() = runTest {
        val http = MockHttp(body = OK_BODY)

        val result = geocoder(http).reverse(37.4224764, -122.0842499)

        val place = assertIs<GeocoderResult.Success<Place>>(result).data.single()
        assertEquals("United States", place.country)
        assertEquals("US", place.isoCountryCode)
        assertEquals("Mountain View", place.locality)
        val request = http.requests.single()
        assertEquals("37.4224764,-122.0842499", request.url.parameters["latlng"])
        assertEquals("secret", request.url.parameters["key"])
    }

    @Test
    fun forwardSendsConfiguredLocationType() = runTest {
        val http = MockHttp(body = OK_BODY)

        geocoder(http) { locationType(GoogleMapsLocationType.Rooftop) }.forward("x")

        assertTrue(http.requests.single().url.toString().contains("ROOFTOP"))
    }

    @Test
    fun zeroResultsIsNotFound() = runTest {
        val http = MockHttp(body = """{"results":[],"status":"ZERO_RESULTS"}""")

        assertEquals(GeocoderResult.NotFound, geocoder(http).forward("nowhere"))
        assertEquals(GeocoderResult.NotFound, geocoder(http).reverse(0.0, 0.0))
    }

    @Test
    fun providerErrorStatusIsGeocodeFailedWithMessage() = runTest {
        val http = MockHttp(
            body = """{"status":"REQUEST_DENIED","error_message":"The provided API key is invalid.","results":[]}""",
        )

        val result = geocoder(http).forward("x")

        val failed = assertIs<GeocoderResult.GeocodeFailed>(result)
        assertEquals(
            "Geocoding failed: [RequestDenied] The provided API key is invalid.",
            failed.message,
        )
    }

    @Test
    fun serverErrorWithoutExpectSuccessIsGeocodeFailed() = runTest {
        val http = MockHttp(
            status = HttpStatusCode.InternalServerError,
            body = "{}",
            expectSuccess = false,
        )

        val result = geocoder(http).forward("x")

        val failed = assertIs<GeocoderResult.GeocodeFailed>(result)
        assertEquals("HTTP request failed with status code 500", failed.message)
    }

    @Test
    fun serverErrorWithExpectSuccessIsGeocodeFailed() = runTest {
        val http = MockHttp(status = HttpStatusCode.Forbidden, body = "{}")

        assertIs<GeocoderResult.GeocodeFailed>(geocoder(http).reverse(1.0, 2.0))
    }

    @Test
    fun malformedBodyIsGeocodeFailed() = runTest {
        val http = MockHttp(body = "not json")

        assertIs<GeocoderResult.GeocodeFailed>(geocoder(http).forward("x"))
    }

    @Test
    fun geocoderIsAvailable() = runTest {
        assertTrue(geocoder(MockHttp(body = OK_BODY)).isAvailable())
    }

    private companion object {

        const val OK_BODY = """
            {"results":[{
              "address_components":[
                {"long_name":"Mountain View","short_name":"Mountain View","types":["locality","political"]},
                {"long_name":"United States","short_name":"US","types":["country","political"]}
              ],
              "formatted_address":"Mountain View, CA, USA",
              "geometry":{"location":{"lat":37.4224764,"lng":-122.0842499}}
            }],"status":"OK"}
        """
    }
}
