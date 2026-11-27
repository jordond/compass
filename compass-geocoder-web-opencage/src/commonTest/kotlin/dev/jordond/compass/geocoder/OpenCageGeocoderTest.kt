package dev.jordond.compass.geocoder

import dev.jordond.compass.test.MockHttp
import dev.jordond.compass.test.fixtures.OpenCageFixtures
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class OpenCageGeocoderTest {

    private val http = MockHttp(body = OpenCageFixtures.geocodeJson)

    private val defaults = mapOf("limit" to "10", "no_dedupe" to "0", "no_record" to "0")

    @Test
    fun forwardSendsTheQueryDefaultsAndKeyAndReturnsCoordinates() = runTest {
        val geocoder = OpenCageGeocoder(
            apiKey = "secret",
            client = http.client,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val result = geocoder.forward("1 Main St & Zürich")

        assertEquals(listOf(OpenCageFixtures.coordinates), result.getOrNull())
        assertEquals("https://api.opencagedata.com/geocode/v1/json", http.urlWithoutQuery)
        assertEquals(mapOf("q" to "1 Main St & Zürich") + defaults + ("key" to "secret"), http.query)
    }

    @Test
    fun reverseSendsTheCoordinatesDefaultsAndKeyAndReturnsPlaces() = runTest {
        val geocoder = OpenCageGeocoder(
            apiKey = "secret",
            client = http.client,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        val result = geocoder.reverse(52.5200066, 13.404954)

        assertEquals(listOf(OpenCageFixtures.place), result.getOrNull())
        assertEquals("https://api.opencagedata.com/geocode/v1/json", http.urlWithoutQuery)
        assertEquals(mapOf("q" to "52.5200066 13.404954") + defaults + ("key" to "secret"), http.query)
    }

    @Test
    fun forwardSendsEachOptionAsItsOwnParameter() = runTest {
        val geocoder = OpenCageGeocoder(
            apiKey = "secret",
            client = http.client,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
        ) {
            limit = 3
            countryCode("ca", "us")
            noDedupe = true
            noRecord = true
        }

        geocoder.forward("Paris")

        assertEquals(
            mapOf(
                "q" to "Paris",
                "limit" to "3",
                "countrycode" to "ca,us",
                "no_dedupe" to "1",
                "no_record" to "1",
                "key" to "secret",
            ),
            http.query,
        )
    }
}
