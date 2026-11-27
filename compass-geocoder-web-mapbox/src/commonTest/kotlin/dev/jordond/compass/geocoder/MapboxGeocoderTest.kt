package dev.jordond.compass.geocoder

import dev.jordond.compass.geocoder.web.parameter.MapboxParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.MapboxTypes
import dev.jordond.compass.geocoder.web.parameter.MapboxTypesList
import dev.jordond.compass.test.MockHttp
import dev.jordond.compass.test.fixtures.MapboxFixtures
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MapboxGeocoderTest {

    private val http = MockHttp(body = MapboxFixtures.geocodeJson)

    private fun TestScope.geocoder(): Geocoder = MapboxGeocoder(
        apiKey = "token",
        client = http.client,
        dispatcher = UnconfinedTestDispatcher(testScheduler),
    )

    private fun TestScope.geocoder(block: MapboxParametersBuilder.() -> Unit): Geocoder =
        MapboxGeocoder(
            apiKey = "token",
            client = http.client,
            dispatcher = UnconfinedTestDispatcher(testScheduler),
            block = block,
        )

    @Test
    fun forwardSendsTheQueryDefaultsAndTokenAndReturnsCoordinates() = runTest {
        val result = geocoder().forward("1 Main St & Zürich")

        assertEquals(listOf(MapboxFixtures.coordinates), result.getOrNull())
        assertEquals("https://api.mapbox.com/search/geocode/v6/forward", http.urlWithoutQuery)
        assertEquals(
            mapOf(
                "q" to "1 Main St & Zürich",
                "permanent" to "false",
                "autocomplete" to "true",
                "access_token" to "token",
            ),
            http.query,
        )
    }

    @Test
    fun reverseSendsTheCoordinatesDefaultsAndTokenAndReturnsPlaces() = runTest {
        val result = geocoder().reverse(38.897, -77.036)

        assertEquals(listOf(MapboxFixtures.place), result.getOrNull())
        assertEquals("https://api.mapbox.com/search/geocode/v6/reverse", http.urlWithoutQuery)
        assertEquals(
            mapOf(
                "latitude" to "38.897",
                "longitude" to "-77.036",
                "permanent" to "false",
                "autocomplete" to "true",
                "access_token" to "token",
            ),
            http.query,
        )
    }

    @Test
    fun forwardSendsEachOptionAsItsOwnParameter() = runTest {
        val geocoder = geocoder {
            limit = 2
            permanent = true
            autocomplete = false
            boundingBox(-79.5, 43.5, -79.25, 43.75)
            country = "ca,us"
            language = "fr"
            proximity = "-79.5,43.5"
            types = MapboxTypesList(MapboxTypes.Place, MapboxTypes.Address)
        }

        geocoder.forward("Paris")

        assertEquals(
            mapOf(
                "q" to "Paris",
                "permanent" to "true",
                "autocomplete" to "false",
                "limit" to "2",
                "bbox" to "-79.5,43.5,-79.25,43.75",
                "country" to "ca,us",
                "language" to "fr",
                "proximity" to "-79.5,43.5",
                "types" to "place,address",
                "access_token" to "token",
            ),
            http.query,
        )
    }
}
