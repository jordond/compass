package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsLocationType
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsLocationTypeList
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsParameters
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GoogleMapsEndpointTest {

    private val base = "https://maps.googleapis.com/maps/api/geocode/json"

    @Test
    fun forwardUrlUsesBaseUrlAddressAndKey() {
        val url = GoogleMapsForwardEndpoint("secret").url("Toronto")

        assertEquals("$base?address=Toronto&key=secret", url)
    }

    @Test
    fun forwardUrlEncodesSpacesInTheQuery() {
        val url = GoogleMapsForwardEndpoint("secret").url("1600 Amphitheatre Parkway")

        assertTrue(url.startsWith("$base?address=1600%20Amphitheatre%20Parkway&"), url)
    }

    @Test
    fun forwardUrlEncodesNonAsciiCharacters() {
        val url = GoogleMapsForwardEndpoint("secret").url("Zürich")

        assertTrue(url.startsWith("$base?address=Z%C3%BCrich&"), url)
    }

    @Test
    fun forwardUrlEndsWithKeyAfterParameters() {
        val endpoint = GoogleMapsForwardEndpoint("secret") {
            locationType(GoogleMapsLocationType.Rooftop)
        }

        val url = endpoint.url("Toronto")

        assertTrue(url.startsWith("$base?address=Toronto&"), url)
        assertTrue(url.endsWith("&key=secret"), url)
        assertTrue(url.contains("ROOFTOP"), url)
    }

    @Test
    fun reverseUrlUsesLatLngAndKey() {
        val url = GoogleMapsReverseEndpoint("secret").url(Coordinates(43.65, -79.38))

        assertEquals("$base?latlng=43.65,-79.38&key=secret", url)
    }

    @Test
    fun reverseUrlIncludesParametersBeforeKey() {
        val endpoint = GoogleMapsReverseEndpoint(
            apiKey = "secret",
            parameters = GoogleMapsParameters(
                GoogleMapsLocationTypeList(
                    GoogleMapsLocationType.Approximate,
                ),
            ),
        )

        val url = endpoint.url(Coordinates(1.5, 2.5))

        assertTrue(url.startsWith("$base?latlng=1.5,2.5&"), url)
        assertTrue(url.endsWith("&key=secret"), url)
        assertTrue(url.contains("APPROXIMATE"), url)
    }
}
