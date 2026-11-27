package dev.jordond.compass.geocoder

import dev.jordond.compass.geocoder.web.parameter.GoogleMapsLocationType
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsParametersBuilder
import dev.jordond.compass.test.MockHttp
import dev.jordond.compass.test.fixtures.GoogleMapsFixtures
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class GoogleMapsGeocoderTest {

    private val http = MockHttp(body = GoogleMapsFixtures.geocodeJson)

    private fun TestScope.geocoder(
        http: MockHttp = this@GoogleMapsGeocoderTest.http,
        block: GoogleMapsParametersBuilder.() -> Unit = {},
    ): Geocoder = GoogleMapsGeocoder(
        apiKey = "secret",
        client = http.client,
        dispatcher = UnconfinedTestDispatcher(testScheduler),
        block = block,
    )

    @Test
    fun forwardSendsTheAddressAndKeyAndReturnsCoordinates() = runTest {
        val result = geocoder().forward("1 Main St & Zürich")

        assertEquals(listOf(GoogleMapsFixtures.coordinates), result.getOrNull())
        assertEquals("https://maps.googleapis.com/maps/api/geocode/json", http.urlWithoutQuery)
        assertEquals(mapOf("address" to "1 Main St & Zürich", "key" to "secret"), http.query)
    }

    @Test
    fun reverseSendsTheCoordinatesAndKeyAndReturnsPlaces() = runTest {
        val result = geocoder().reverse(37.4224764, -122.0842499)

        assertEquals(listOf(GoogleMapsFixtures.place), result.getOrNull())
        assertEquals("https://maps.googleapis.com/maps/api/geocode/json", http.urlWithoutQuery)
        assertEquals(mapOf("latlng" to "37.4224764,-122.0842499", "key" to "secret"), http.query)
    }

    @Test
    fun forwardSendsLocationTypesAsTheirOwnParameter() = runTest {
        val geocoder = geocoder {
            locationTypes(GoogleMapsLocationType.Rooftop, GoogleMapsLocationType.RangeInterpolated)
        }

        geocoder.forward("Paris")

        assertEquals(
            mapOf(
                "address" to "Paris",
                "location_type" to "ROOFTOP|RANGE_INTERPOLATED",
                "key" to "secret",
            ),
            http.query,
        )
    }

    @Test
    fun reverseSendsLocationTypesAsTheirOwnParameter() = runTest {
        val geocoder = geocoder { locationType(GoogleMapsLocationType.Approximate) }

        geocoder.reverse(1.5, 2.5)

        assertEquals(
            mapOf("latlng" to "1.5,2.5", "location_type" to "APPROXIMATE", "key" to "secret"),
            http.query,
        )
    }

    @Test
    fun zeroResultsStatusIsNotFound() = runTest {
        val empty = MockHttp(body = """{"results":[],"status":"ZERO_RESULTS"}""")

        assertEquals(GeocoderResult.NotFound, geocoder(empty).forward("nowhere"))
    }

    @Test
    fun errorStatusInASuccessfulResponseIsGeocodeFailedWithTheProviderMessage() = runTest {
        val denied = MockHttp(
            body = """{"status":"REQUEST_DENIED","error_message":"The provided API key is invalid.","results":[]}""",
        )

        val result = geocoder(denied).reverse(1.5, 2.5)

        assertEquals(
            "Geocoding failed: [RequestDenied] The provided API key is invalid.",
            assertIs<GeocoderResult.GeocodeFailed>(result).message,
        )
    }
}
