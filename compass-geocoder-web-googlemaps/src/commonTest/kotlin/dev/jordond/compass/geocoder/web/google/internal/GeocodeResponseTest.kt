package dev.jordond.compass.geocoder.web.google.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.exception.GeocodeException
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

        assertEquals(StatusResponse.Ok, response.status)
        assertNull(response.errorMessage)
        assertEquals(1, response.results.size)
        val result = response.results.single()
        assertEquals("1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA", result.formattedAddress)
        assertEquals(7, result.addressComponents.size)
        assertEquals(LocationResponse(37.4224764, -122.0842499), result.geometry?.location)
    }

    @Test
    fun mapsFullPayloadToPlace() {
        val places = decode(FULL).results.toPlaces()

        val expected = Place(
            coordinates = Coordinates(37.4224764, -122.0842499),
            name = "Amphitheatre Parkway",
            street = "1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA",
            isoCountryCode = "US",
            country = "United States",
            postalCode = "94043",
            administrativeArea = "California",
            subAdministrativeArea = "Santa Clara County",
            locality = "Mountain View",
            subLocality = null,
            thoroughfare = "Amphitheatre Parkway",
            subThoroughfare = null,
        )
        assertEquals(listOf(expected), places)
    }

    @Test
    fun mapsFullPayloadToCoordinates() {
        val coordinates = decode(FULL).results.toCoordinates()

        assertEquals(listOf(Coordinates(37.4224764, -122.0842499)), coordinates)
    }

    @Test
    fun mapsMultipleResultsInOrder() {
        val response = decode(
            """
            {"status":"OK","results":[
              {"formatted_address":"A","geometry":{"location":{"lat":1.0,"lng":2.0}}},
              {"formatted_address":"B","geometry":{"location":{"lat":3.0,"lng":4.0}}}
            ]}
            """.trimIndent(),
        )

        assertEquals(
            listOf(Coordinates(1.0, 2.0), Coordinates(3.0, 4.0)),
            response.results.toCoordinates(),
        )
        assertEquals(listOf("A", "B"), response.results.toPlaces().map { it.street })
    }

    @Test
    fun localityFallsBackToAdministrativeAreaLevel3() {
        val response = decode(
            """
            {"status":"OK","results":[{
              "address_components":[
                {"long_name":"Ward 5","short_name":"W5","types":["administrative_area_level_3"]}
              ],
              "geometry":{"location":{"lat":1.0,"lng":2.0}}
            }]}
            """.trimIndent(),
        )

        assertEquals("Ward 5", response.results.toPlaces().single().locality)
    }

    @Test
    fun localityPrefersLocalityOverAdministrativeAreaLevel3() {
        val response = decode(
            """
            {"status":"OK","results":[{
              "address_components":[
                {"long_name":"Ward 5","short_name":"W5","types":["administrative_area_level_3"]},
                {"long_name":"Springfield","short_name":"Springfield","types":["locality"]}
              ],
              "geometry":{"location":{"lat":1.0,"lng":2.0}}
            }]}
            """.trimIndent(),
        )

        assertEquals("Springfield", response.results.toPlaces().single().locality)
    }

    @Test
    fun mapsNeighborhoodToSubLocality() {
        val response = decode(
            """
            {"status":"OK","results":[{
              "address_components":[
                {"long_name":"Kensington","short_name":"Kensington","types":["neighborhood","political"]}
              ],
              "geometry":{"location":{"lat":1.0,"lng":2.0}}
            }]}
            """.trimIndent(),
        )

        assertEquals("Kensington", response.results.toPlaces().single().subLocality)
    }

    @Test
    fun resultsWithoutGeometryAreSkipped() {
        val response = decode(
            """
            {"status":"OK","results":[
              {"formatted_address":"No geometry"},
              {"formatted_address":"No location","geometry":{}},
              {"formatted_address":"Ok","geometry":{"location":{"lat":1.0,"lng":2.0}}}
            ]}
            """.trimIndent(),
        )

        assertEquals(listOf(Coordinates(1.0, 2.0)), response.results.toCoordinates())
        assertEquals(listOf("Ok"), response.results.toPlaces().map { it.street })
    }

    @Test
    fun placesWithoutAnyAddressInformationAreSkipped() {
        val response = decode(
            """{"status":"OK","results":[{"geometry":{"location":{"lat":1.0,"lng":2.0}}}]}""",
        )

        assertEquals(1, response.results.toCoordinates().size)
        assertTrue(response.results.toPlaces().isEmpty())
    }

    @Test
    fun missingComponentTypesLeaveFieldsNull() {
        val response = decode(
            """
            {"status":"OK","results":[{
              "formatted_address":"Somewhere",
              "geometry":{"location":{"lat":1.0,"lng":2.0}}
            }]}
            """.trimIndent(),
        )

        val place = response.results.toPlaces().single()

        assertEquals("Somewhere", place.street)
        assertNull(place.name)
        assertNull(place.country)
        assertNull(place.isoCountryCode)
        assertNull(place.postalCode)
        assertNull(place.locality)
    }

    @Test
    fun zeroResultsDecodesWithoutResultsKeyAndIsNotAnError() {
        val response = decode("""{"status":"ZERO_RESULTS"}""")

        assertTrue(response.resultsOrThrow().isEmpty())
    }

    @Test
    fun okStatusReturnsResults() {
        assertEquals(1, decode(FULL).resultsOrThrow().size)
    }

    @Test
    fun errorStatusesThrowGeocodeExceptionWithStatusAndMessage() {
        val statuses = mapOf(
            "OVER_QUERY_LIMIT" to "OverQueryLimit",
            "REQUEST_DENIED" to "RequestDenied",
            "INVALID_REQUEST" to "InvalidRequest",
            "UNKNOWN_ERROR" to "UnknownError",
        )

        statuses.forEach { (raw, name) ->
            val response = decode("""{"status":"$raw","error_message":"boom","results":[]}""")

            val error = assertFailsWith<GeocodeException> { response.resultsOrThrow() }

            assertEquals("Geocoding failed: [$name] boom", error.message)
        }
    }

    @Test
    fun errorStatusWithoutMessageStillThrows() {
        val response = decode("""{"status":"REQUEST_DENIED"}""")

        val error = assertFailsWith<GeocodeException> { response.resultsOrThrow() }

        assertTrue(error.message.orEmpty().contains("RequestDenied"))
    }

    @Test
    fun unknownStatusFailsToDecode() {
        assertFailsWith<SerializationException> { decode("""{"status":"SOMETHING_NEW"}""") }
    }

    @Test
    fun missingStatusFailsToDecode() {
        assertFailsWith<SerializationException> { decode("""{"results":[]}""") }
    }

    private companion object {

        const val FULL = """
            {"results":[{
              "address_components":[
                {"long_name":"1600","short_name":"1600","types":["street_number"]},
                {"long_name":"Amphitheatre Parkway","short_name":"Amphitheatre Pkwy","types":["route"]},
                {"long_name":"Mountain View","short_name":"Mountain View","types":["locality","political"]},
                {"long_name":"Santa Clara County","short_name":"Santa Clara County","types":["administrative_area_level_2","political"]},
                {"long_name":"California","short_name":"CA","types":["administrative_area_level_1","political"]},
                {"long_name":"United States","short_name":"US","types":["country","political"]},
                {"long_name":"94043","short_name":"94043","types":["postal_code"]}
              ],
              "formatted_address":"1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA",
              "geometry":{
                "location":{"lat":37.4224764,"lng":-122.0842499},
                "location_type":"ROOFTOP",
                "viewport":{"northeast":{"lat":37.4238,"lng":-122.0829},"southwest":{"lat":37.4211,"lng":-122.0856}}
              },
              "place_id":"ChIJ2eUgeAK6j4ARbn5u_wAGqWA",
              "types":["street_address"]
            }],"status":"OK"}
        """
    }
}
