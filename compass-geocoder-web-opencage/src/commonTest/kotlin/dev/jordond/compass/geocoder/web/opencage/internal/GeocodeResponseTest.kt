package dev.jordond.compass.geocoder.web.opencage.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.test.fixtures.OpenCageFixtures
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
        val places = decode(OpenCageFixtures.geocodeJson).toPlaces()

        assertEquals(listOf(OpenCageFixtures.place), places)
    }

    @Test
    fun mapsFullPayloadToCoordinates() {
        assertEquals(listOf(OpenCageFixtures.coordinates), decode(OpenCageFixtures.geocodeJson).toCoordinates())
    }

    @Test
    fun emptyResultsProduceEmptyLists() {
        val response = decode("""{"results":[],"status":{"code":200,"message":"OK"}}""")

        assertTrue(response.toPlaces().isEmpty())
        assertTrue(response.toCoordinates().isEmpty())
    }

    @Test
    fun multipleResultsKeepOrder() {
        val response = decode(
            """
            {"results":[
              {"components":{"road":"A"},"formatted":"A","geometry":{"lat":1.0,"lng":2.0}},
              {"components":{"road":"B"},"formatted":"B","geometry":{"lat":3.0,"lng":4.0}}
            ]}
            """.trimIndent(),
        )

        assertEquals(listOf("A", "B"), response.toPlaces().map { it.street })
        assertEquals(
            listOf(Coordinates(1.0, 2.0), Coordinates(3.0, 4.0)),
            response.toCoordinates(),
        )
    }

    @Test
    fun blankComponentsAreMappedToNull() {
        val response = decode(
            """
            {"results":[{
              "components":{"restaurant":"","road":" ","country":"Germany","postcode":"  "},
              "formatted":"x",
              "geometry":{"lat":1.0,"lng":2.0}
            }]}
            """.trimIndent(),
        )

        val place = response.toPlaces().single()

        assertEquals("Germany", place.country)
        assertNull(place.name)
        assertNull(place.street)
        assertNull(place.thoroughfare)
        assertNull(place.postalCode)
    }

    @Test
    fun resultsWithoutUsableComponentsAreSkippedFromPlacesButNotCoordinates() {
        val response = decode(
            """
            {"results":[{
              "components":{"city":"Ignored","continent":"Europe"},
              "formatted":"Europe",
              "geometry":{"lat":1.0,"lng":2.0}
            }]}
            """.trimIndent(),
        )

        assertTrue(response.toPlaces().isEmpty())
        assertEquals(listOf(Coordinates(1.0, 2.0)), response.toCoordinates())
    }

    @Test
    fun localityComesFromNormalizedCityNotCity() {
        val response = decode(
            """
            {"results":[{
              "components":{"city":"Raw","_normalized_city":"Normalized"},
              "formatted":"x",
              "geometry":{"lat":1.0,"lng":2.0}
            }]}
            """.trimIndent(),
        )

        assertEquals("Normalized", response.toPlaces().single().locality)
    }

    @Test
    fun missingResultsFailsToDecode() {
        assertFailsWith<SerializationException> {
            decode("""{"status":{"code":401,"message":"invalid API key"}}""")
        }
    }

    @Test
    fun resultWithoutGeometryFailsToDecode() {
        assertFailsWith<SerializationException> {
            decode("""{"results":[{"components":{},"formatted":"x"}]}""")
        }
    }
}
