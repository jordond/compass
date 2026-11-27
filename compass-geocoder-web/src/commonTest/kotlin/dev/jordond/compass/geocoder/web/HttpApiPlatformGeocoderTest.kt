package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geocoder.ForwardGeocoder
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.GeocoderResult
import dev.jordond.compass.geocoder.ReverseGeocoder
import dev.jordond.compass.test.MockHttp
import dev.jordond.compass.test.place
import dev.jordond.compass.tools.web.exception.WebException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class HttpApiPlatformGeocoderTest {

    private val http = MockHttp(body = "{}")

    private val coordinates = Coordinates(1.5, 2.5)

    private val place = place(coordinates = coordinates, name = "Somewhere")

    private val forwardEndpoint = ForwardEndpoint(
        url = { address -> "https://api.test/forward?q=$address" },
        mapResponse = { listOf(coordinates) },
    )

    private val reverseEndpoint = ReverseEndpoint(
        url = { target -> "https://api.test/reverse?lat=${target.latitude}&lng=${target.longitude}" },
        mapResponse = { listOf(place) },
    )

    @Test
    fun forwardPassesTheRawAddressToTheEndpoint() = runTest {
        val addresses = mutableListOf<String>()
        val endpoint = ForwardEndpoint(
            url = { address ->
                addresses += address
                "https://api.test/forward"
            },
            mapResponse = { emptyList() },
        )

        ForwardHttpApiPlatformGeocoder(endpoint, http.client).forward("1 Main St & Co")

        assertEquals(listOf("1 Main St & Co"), addresses)
    }

    @Test
    fun forwardRequestsTheEndpointUrlAndReturnsItsMapping() = runTest {
        val result = ForwardHttpApiPlatformGeocoder(forwardEndpoint, http.client).forward("x")

        assertEquals(listOf(coordinates), result)
        assertEquals("https://api.test/forward?q=x", http.url.toString())
    }

    @Test
    fun reverseRequestsTheEndpointUrlAndReturnsItsMapping() = runTest {
        val result = ReverseHttpApiPlatformGeocoder(reverseEndpoint, http.client).reverse(12.5, -3.25)

        assertEquals(listOf(place), result)
        assertEquals("https://api.test/reverse?lat=12.5&lng=-3.25", http.url.toString())
    }

    @Test
    fun forwardGeocoderDoesNotSupportReverse() = runTest {
        val geocoder = ForwardHttpApiPlatformGeocoder(forwardEndpoint, http.client)

        assertFailsWith<NotSupportedException> { geocoder.reverse(1.5, 2.5) }
        assertEquals(emptyList(), http.requests)
    }

    @Test
    fun reverseGeocoderDoesNotSupportForward() = runTest {
        val geocoder = ReverseHttpApiPlatformGeocoder(reverseEndpoint, http.client)

        assertFailsWith<NotSupportedException> { geocoder.forward("x") }
        assertEquals(emptyList(), http.requests)
    }

    @Test
    fun httpErrorSurfacesAsWebException() = runTest {
        val failing = MockHttp(body = "{}", status = HttpStatusCode.Forbidden)
        val geocoder = ForwardHttpApiPlatformGeocoder(forwardEndpoint, failing.client)

        val exception = assertFailsWith<WebException> { geocoder.forward("x") }

        assertEquals(HttpStatusCode.Forbidden, exception.statusCode)
    }

    @Test
    fun combinedGeocoderRoutesForwardAndReverseToTheirEndpoints() = runTest {
        val combined = HttpApiPlatformGeocoder(forwardEndpoint, reverseEndpoint, client = http.client)

        combined.forward("a")
        combined.reverse(1.5, 2.5)

        assertEquals(
            listOf("https://api.test/forward?q=a", "https://api.test/reverse?lat=1.5&lng=2.5"),
            http.requests.map { request -> request.url.toString() },
        )
    }

    @Test
    fun geocoderFactoryGeocodesInBothDirections() = runTest {
        val geocoder = Geocoder(
            forwardEndpoint,
            reverseEndpoint,
            httpClient = http.client,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        assertEquals(listOf(coordinates), geocoder.forward("x").getOrNull())
        assertEquals(listOf(place), geocoder.reverse(1.5, 2.5).getOrNull())
    }

    @Test
    fun geocoderFactoryReportsAnHttpErrorAsGeocodeFailed() = runTest {
        val geocoder = Geocoder(
            forwardEndpoint,
            reverseEndpoint,
            httpClient = MockHttp(body = "{}", status = HttpStatusCode.InternalServerError).client,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        assertIs<GeocoderResult.GeocodeFailed>(geocoder.forward("x"))
    }

    @Test
    fun forwardOnlyFactoryReturnsCoordinates() = runTest {
        val geocoder = ForwardGeocoder(
            forwardEndpoint,
            httpClient = http.client,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        assertEquals(listOf(coordinates), geocoder.forward("x").getOrNull())
    }

    @Test
    fun reverseOnlyFactoryReturnsPlaces() = runTest {
        val geocoder = ReverseGeocoder(
            reverseEndpoint,
            httpClient = http.client,
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        assertEquals(listOf(place), geocoder.reverse(1.5, 2.5).getOrNull())
    }
}
