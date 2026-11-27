package dev.jordond.compass.geocoder.web.mapbox.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.test.fixtures.MapboxFixtures
import dev.jordond.compass.tools.web.HttpApiEndpoint
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GeocodeResponseTest {

    private fun decode(body: String): GeocodeResponse =
        HttpApiEndpoint.json().decodeFromString(body)

    @Test
    fun mapsFullPayloadToPlace() {
        val places = decode(MapboxFixtures.geocodeJson).toPlaces()

        assertEquals(listOf(MapboxFixtures.place), places)
    }

    @Test
    fun mapsFullPayloadToCoordinates() {
        assertEquals(listOf(MapboxFixtures.coordinates), decode(MapboxFixtures.geocodeJson).toCoordinates())
    }

    @Test
    fun localityFallsBackToPlace() {
        val response = decode(
            """
            {"features":[{"properties":{
              "name":"x",
              "coordinates":{"latitude":1.0,"longitude":2.0},
              "context":{"place":{"name":"Washington"}}
            }}]}
            """.trimIndent(),
        )

        assertEquals("Washington", response.toPlaces().single().locality)
    }

    @Test
    fun emptyFeaturesProduceEmptyLists() {
        val response = decode("""{"type":"FeatureCollection","features":[]}""")

        assertTrue(response.toPlaces().isEmpty())
        assertTrue(response.toCoordinates().isEmpty())
    }

    @Test
    fun featuresWithoutPropertiesAreSkipped() {
        val response = decode("""{"features":[{},{"properties":{}}]}""")

        assertTrue(response.toPlaces().isEmpty())
        assertTrue(response.toCoordinates().isEmpty())
    }

    @Test
    fun featuresWithoutCoordinatesAreSkipped() {
        val response = decode(
            """{"features":[{"properties":{"name":"x","context":{"country":{"name":"Canada"}}}}]}""",
        )

        assertTrue(response.toPlaces().isEmpty())
        assertTrue(response.toCoordinates().isEmpty())
    }

    @Test
    fun placesWithoutContextAreSkippedButCoordinatesAreKept() {
        val response = decode(
            """{"features":[{"properties":{"name":"x","coordinates":{"latitude":1.0,"longitude":2.0}}}]}""",
        )

        assertTrue(response.toPlaces().isEmpty())
        assertEquals(listOf(Coordinates(1.0, 2.0)), response.toCoordinates())
    }

    @Test
    fun missingContextEntriesLeaveFieldsNull() {
        val response = decode(
            """
            {"features":[{"properties":{
              "name":"x",
              "coordinates":{"latitude":1.0,"longitude":2.0},
              "context":{}
            }}]}
            """.trimIndent(),
        )

        val place = response.toPlaces().single()

        assertEquals("x", place.name)
        assertNull(place.street)
        assertNull(place.country)
        assertNull(place.isoCountryCode)
        assertNull(place.postalCode)
        assertNull(place.locality)
    }

    @Test
    fun multipleFeaturesKeepOrder() {
        val response = decode(
            """
            {"features":[
              {"properties":{"name":"a","coordinates":{"latitude":1.0,"longitude":2.0},"context":{}}},
              {"properties":{"name":"b","coordinates":{"latitude":3.0,"longitude":4.0},"context":{}}}
            ]}
            """.trimIndent(),
        )

        assertEquals(listOf("a", "b"), response.toPlaces().map { it.name })
        assertEquals(
            listOf(Coordinates(1.0, 2.0), Coordinates(3.0, 4.0)),
            response.toCoordinates(),
        )
    }

    @Test
    fun missingFeaturesFailsToDecode() {
        assertFailsWith<SerializationException> { decode("""{"message":"Not Authorized"}""") }
    }
}
