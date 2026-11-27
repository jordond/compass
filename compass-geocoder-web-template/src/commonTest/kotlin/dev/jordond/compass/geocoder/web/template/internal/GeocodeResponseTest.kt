package dev.jordond.compass.geocoder.web.template.internal

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GeocodeResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun decodesResults() {
        val response = json.decodeFromString<GeocodeResponse>(
            """{"results":[{"foo":"a","other":1},{}],"status":"OK"}""",
        )

        assertEquals(2, response.results.size)
        assertEquals("a", response.results[0].foo)
        assertNull(response.results[1].foo)
    }

    @Test
    fun missingResultsDecodesToEmptyList() {
        val response = json.decodeFromString<GeocodeResponse>("{}")

        assertTrue(response.results.isEmpty())
        assertTrue(response.toPlaces().isEmpty())
        assertTrue(response.toCoordinates().isEmpty())
    }
}
