package dev.jordond.compass.geocoder.web.google.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.exception.GeocodeException
import dev.jordond.compass.test.fixtures.GoogleMapsFixtures
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
        val places = decode(GoogleMapsFixtures.geocodeJson).results.toPlaces()

        assertEquals(listOf(GoogleMapsFixtures.place), places)
    }

    @Test
    fun mapsFullPayloadToCoordinates() {
        val coordinates = decode(GoogleMapsFixtures.geocodeJson).results.toCoordinates()

        assertEquals(listOf(GoogleMapsFixtures.coordinates), coordinates)
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
    fun errorStatusWithoutMessageThrowsWithOnlyTheStatus() {
        val response = decode("""{"status":"REQUEST_DENIED"}""")

        val error = assertFailsWith<GeocodeException> { response.resultsOrThrow() }

        assertEquals("Geocoding failed: [RequestDenied]", error.message)
    }

    @Test
    fun unknownStatusFailsToDecode() {
        assertFailsWith<SerializationException> { decode("""{"status":"SOMETHING_NEW"}""") }
    }

    @Test
    fun missingStatusFailsToDecode() {
        assertFailsWith<SerializationException> { decode("""{"results":[]}""") }
    }
}
