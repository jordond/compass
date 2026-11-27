package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geocoder.ForwardGeocoder
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.GeocoderResult
import dev.jordond.compass.geocoder.ReverseGeocoder
import dev.jordond.compass.tools.web.exception.WebException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class HttpApiPlatformGeocoderTest {

    private val requested = mutableListOf<String>()

    private fun client(status: HttpStatusCode = HttpStatusCode.OK) = HttpClient(
        MockEngine { request ->
            requested += request.url.toString()
            if (status.value in 200..299) respond("ok", status) else respondError(status)
        },
    )

    private val forwardEndpoint = ForwardEndpoint(
        url = { "https://api.test/forward/$it" },
        mapResponse = { listOf(Coordinates(1.0, 2.0)) },
    )

    private val reverseEndpoint = ReverseEndpoint(
        url = { "https://api.test/reverse/${it.latitude}/${it.longitude}" },
        mapResponse = { listOf(place()) },
    )

    private fun place() = Place(
        Coordinates(1.0, 2.0), "n", null, null, null, null, null, null, null, null, null, null,
    )

    @Test
    fun forwardPercentEncodesAddressBeforeBuildingUrl() = runTest {
        ForwardHttpApiPlatformGeocoder(forwardEndpoint, client()).forward("1 Main St & Co")

        assertEquals(listOf("https://api.test/forward/1%20Main%20St%20%26%20Co"), requested)
    }

    @Test
    fun forwardReturnsMappedCoordinates() = runTest {
        val result = ForwardHttpApiPlatformGeocoder(forwardEndpoint, client()).forward("x")
        assertEquals(listOf(Coordinates(1.0, 2.0)), result)
    }

    @Test
    fun forwardGeocoderDoesNotSupportReverse() = runTest {
        val geocoder = ForwardHttpApiPlatformGeocoder(forwardEndpoint, client())
        assertTrue(geocoder.isAvailable())
        assertFailsWith<NotSupportedException> { geocoder.reverse(1.0, 2.0) }
        assertEquals(emptyList(), requested)
    }

    @Test
    fun reverseBuildsUrlFromCoordinates() = runTest {
        val result = ReverseHttpApiPlatformGeocoder(reverseEndpoint, client()).reverse(12.5, -3.25)

        assertEquals(listOf("https://api.test/reverse/12.5/-3.25"), requested)
        assertEquals(1, result.size)
    }

    @Test
    fun reverseGeocoderDoesNotSupportForward() = runTest {
        val geocoder = ReverseHttpApiPlatformGeocoder(reverseEndpoint, client())
        assertTrue(geocoder.isAvailable())
        assertFailsWith<NotSupportedException> { geocoder.forward("x") }
        assertEquals(emptyList(), requested)
    }

    @Test
    fun serverErrorSurfacesAsWebException() = runTest {
        val geocoder = ForwardHttpApiPlatformGeocoder(forwardEndpoint, client(HttpStatusCode.Forbidden))
        val exception = assertFailsWith<WebException> { geocoder.forward("x") }
        assertEquals(HttpStatusCode.Forbidden, exception.statusCode)
    }

    @Test
    fun combinedGeocoderRoutesForwardAndReverseToTheirEndpoints() = runTest {
        val combined = HttpApiPlatformGeocoder(forwardEndpoint, reverseEndpoint, client = client())

        assertTrue(combined.isAvailable())
        combined.forward("a")
        combined.reverse(1.5, 2.5)

        assertEquals(
            listOf("https://api.test/forward/a", "https://api.test/reverse/1.5/2.5"),
            requested,
        )
    }

    @Test
    fun webGeocoderMapsHttpFailureToGeocodeFailed() = runTest {
        val geocoder = Geocoder(
            forwardEndpoint,
            reverseEndpoint,
            httpClient = client(HttpStatusCode.InternalServerError),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = geocoder.forward("x")

        assertIs<GeocoderResult.GeocodeFailed>(result)
        assertEquals("HTTP request failed with status code 500", result.message)
    }

    @Test
    fun webGeocoderReturnsSuccessForBothDirections() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val geocoder = Geocoder(forwardEndpoint, reverseEndpoint, httpClient = client(), dispatcher = dispatcher)

        assertTrue(geocoder.isAvailable())
        assertIs<GeocoderResult.Success<*>>(geocoder.forward("x"))
        assertIs<GeocoderResult.Success<*>>(geocoder.reverse(1.0, 2.0))
    }

    @Test
    fun forwardOnlyGeocoderFactoryReturnsCoordinates() = runTest {
        val geocoder = ForwardGeocoder(
            forwardEndpoint,
            httpClient = client(),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = geocoder.forward("x")

        assertIs<GeocoderResult.Success<Coordinates>>(result)
        assertEquals(listOf(Coordinates(1.0, 2.0)), result.data)
    }

    @Test
    fun reverseOnlyGeocoderFactoryReturnsPlaces() = runTest {
        val geocoder = ReverseGeocoder(
            reverseEndpoint,
            httpClient = client(),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        assertIs<GeocoderResult.Success<Place>>(geocoder.reverse(1.0, 2.0))
    }

    @Test
    fun forwardOnlyGeocoderReverseIsNotSupported() = runTest {
        val geocoder = Geocoder(
            ForwardHttpApiPlatformGeocoder(forwardEndpoint, client()),
            StandardTestDispatcher(testScheduler),
        )
        assertEquals(GeocoderResult.NotSupported, geocoder.reverse(1.0, 2.0))
    }
}
