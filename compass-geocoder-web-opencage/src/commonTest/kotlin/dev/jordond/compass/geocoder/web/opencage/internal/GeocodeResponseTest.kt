package dev.jordond.compass.geocoder.web.opencage.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GeocodeResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun decode(body: String): GeocodeResponse = json.decodeFromString(body)

    @Test
    fun decodesFullPayload() {
        val result = decode(FULL).results.single()

        assertEquals("Berlin", result.components.normalizedCity)
        assertEquals("de", result.components.countryCode)
        assertEquals(52.5200066, result.geometry.lat)
        assertEquals(13.404954, result.geometry.lng)
        assertEquals("Friedrichstrasse, 10117 Berlin, Germany", result.formatted)
    }

    @Test
    fun mapsFullPayloadToPlace() {
        val places = decode(FULL).toPlaces()

        val expected = Place(
            coordinates = Coordinates(52.5200066, 13.404954),
            name = "Zur letzten Instanz",
            street = "Friedrichstrasse",
            isoCountryCode = "de",
            country = "Germany",
            postalCode = "10117",
            administrativeArea = "Berlin",
            subAdministrativeArea = null,
            locality = "Berlin",
            subLocality = "Mitte",
            thoroughfare = "Friedrichstrasse",
            subThoroughfare = null,
        )
        assertEquals(listOf(expected), places)
    }

    @Test
    fun mapsFullPayloadToCoordinates() {
        assertEquals(listOf(Coordinates(52.5200066, 13.404954)), decode(FULL).toCoordinates())
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

    private companion object {

        const val FULL = """
            {
              "documentation":"https://opencagedata.com/api",
              "rate":{"limit":2500,"remaining":2499,"reset":1700000000},
              "results":[{
                "components":{
                  "ISO_3166-1_alpha-2":"DE",
                  "_category":"commerce",
                  "_normalized_city":"Berlin",
                  "_type":"restaurant",
                  "city":"Berlin",
                  "continent":"Europe",
                  "country":"Germany",
                  "country_code":"de",
                  "postcode":"10117",
                  "restaurant":"Zur letzten Instanz",
                  "road":"Friedrichstrasse",
                  "state":"Berlin",
                  "suburb":"Mitte"
                },
                "confidence":9,
                "formatted":"Friedrichstrasse, 10117 Berlin, Germany",
                "geometry":{"lat":52.5200066,"lng":13.404954}
              }],
              "status":{"code":200,"message":"OK"},
              "total_results":1
            }
        """
    }
}
