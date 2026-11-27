package dev.jordond.compass.geocoder.web.mapbox.internal

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
        val response = decode(FULL)

        val properties = response.features.single().properties
        assertEquals("123 Main Street", properties?.name)
        assertEquals(CoordinatesResponse(latitude = 38.897, longitude = -77.036), properties?.coordinates)
        assertEquals("Main Street", properties?.context?.street?.name)
        assertEquals("US", properties?.context?.country?.countryCode)
        assertEquals("DC", properties?.context?.region?.regionCode)
    }

    @Test
    fun mapsFullPayloadToPlace() {
        val places = decode(FULL).toPlaces()

        val expected = Place(
            coordinates = Coordinates(38.897, -77.036),
            name = "123 Main Street",
            street = "Main Street",
            isoCountryCode = "US",
            country = "United States",
            postalCode = "20500",
            administrativeArea = "District of Columbia",
            subAdministrativeArea = "Ward 2",
            locality = "Georgetown",
            subLocality = "Foggy Bottom",
            thoroughfare = "Main Street",
            subThoroughfare = null,
        )
        assertEquals(listOf(expected), places)
    }

    @Test
    fun mapsFullPayloadToCoordinates() {
        assertEquals(listOf(Coordinates(38.897, -77.036)), decode(FULL).toCoordinates())
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

    private companion object {

        const val FULL = """
            {"type":"FeatureCollection","features":[{
              "type":"Feature",
              "id":"dXJuOm1ieGFkcjo",
              "geometry":{"type":"Point","coordinates":[-77.036,38.897]},
              "properties":{
                "mapbox_id":"dXJuOm1ieGFkcjo",
                "feature_type":"address",
                "name":"123 Main Street",
                "coordinates":{"longitude":-77.036,"latitude":38.897,"accuracy":"rooftop"},
                "context":{
                  "address":{"name":"123 Main Street","address_number":"123","street_name":"Main Street"},
                  "street":{"name":"Main Street"},
                  "neighbourhood":{"name":"Foggy Bottom"},
                  "postcode":{"name":"20500"},
                  "locality":{"name":"Georgetown"},
                  "place":{"name":"Washington"},
                  "district":{"name":"Ward 2"},
                  "region":{"name":"District of Columbia","region_code":"DC"},
                  "country":{"name":"United States","country_code":"US"}
                }
              }
            }],"attribution":"NOTICE: (c) 2024 Mapbox"}
        """
    }
}
