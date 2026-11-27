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
class MapboxGeocoderTest {

    private fun TestScope.geocoder(http: MockHttp): Geocoder = MapboxGeocoder(
        apiKey = "token",
        client = http.client,
        dispatcher = UnconfinedTestDispatcher(testScheduler),
    )

    @Test
    fun forwardReturnsCoordinates() = runTest {
        val http = MockHttp(body = OK_BODY)

        val result = geocoder(http).forward("Washington")

        assertEquals(
            listOf(Coordinates(38.897, -77.036)),
            assertIs<GeocoderResult.Success<Coordinates>>(result).data,
        )
        val request = http.requests.single()
        assertEquals("api.mapbox.com", request.url.host)
        assertEquals("/search/geocode/v6/forward", request.url.encodedPath)
        assertEquals("token", request.url.parameters["access_token"])
    }

    @Test
    fun reverseReturnsPlaces() = runTest {
        val http = MockHttp(body = OK_BODY)

        val result = geocoder(http).reverse(38.897, -77.036)

        val place = assertIs<GeocoderResult.Success<Place>>(result).data.single()
        assertEquals("US", place.isoCountryCode)
        assertEquals("United States", place.country)
        val request = http.requests.single()
        assertEquals("/search/geocode/v6/reverse", request.url.encodedPath)
        assertEquals("38.897", request.url.parameters["latitude"])
        assertEquals("-77.036", request.url.parameters["longitude"])
        assertEquals("token", request.url.parameters["access_token"])
    }

    @Test
    fun emptyFeaturesIsNotFound() = runTest {
        val http = MockHttp(body = """{"type":"FeatureCollection","features":[]}""")

        assertEquals(GeocoderResult.NotFound, geocoder(http).forward("nowhere"))
        assertEquals(GeocoderResult.NotFound, geocoder(http).reverse(0.0, 0.0))
    }

    @Test
    fun unauthorizedResponseIsGeocodeFailed() = runTest {
        val http = MockHttp(
            status = HttpStatusCode.Unauthorized,
            body = """{"message":"Not Authorized - Invalid Token"}""",
        )

        assertIs<GeocoderResult.GeocodeFailed>(geocoder(http).forward("x"))
    }

    @Test
    fun serverErrorWithoutExpectSuccessReportsStatusCode() = runTest {
        val http = MockHttp(
            status = HttpStatusCode.BadGateway,
            body = "{}",
            expectSuccess = false,
        )

        val failed = assertIs<GeocoderResult.GeocodeFailed>(geocoder(http).reverse(1.0, 2.0))

        assertEquals("HTTP request failed with status code 502", failed.message)
    }

    @Test
    fun malformedBodyIsGeocodeFailed() = runTest {
        assertIs<GeocoderResult.GeocodeFailed>(geocoder(MockHttp(body = "<html>")).forward("x"))
    }

    @Test
    fun parametersAreSentWithTheRequest() = runTest {
        val http = MockHttp(body = OK_BODY)
        val geocoder = MapboxGeocoder(
            apiKey = "token",
            client = http.client,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        ) {
            language = "fr"
        }

        geocoder.forward("x")

        assertTrue(http.requests.single().url.toString().contains("language"))
    }

    private companion object {

        const val OK_BODY = """
            {"type":"FeatureCollection","features":[{
              "properties":{
                "name":"Washington",
                "coordinates":{"longitude":-77.036,"latitude":38.897},
                "context":{
                  "place":{"name":"Washington"},
                  "country":{"name":"United States","country_code":"US"}
                }
              }
            }]}
        """
    }
}
