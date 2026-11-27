package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.web.parameter.MapboxParameters
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MapboxEndpointTest {

    private val base = "https://api.mapbox.com/search/geocode/v6"

    @Test
    fun forwardUrlUsesBaseUrlQueryAndAccessToken() {
        val url = MapboxForwardEndpoint("token").url("Toronto")

        assertTrue(url.startsWith("$base/forward?q=Toronto&"), url)
        assertTrue(url.endsWith("&access_token=token"), url)
    }

    @Test
    fun forwardUrlEncodesSpacesAndNonAsciiCharacters() {
        val url = MapboxForwardEndpoint("token").url("Zürich Hauptbahnhof")

        assertTrue(url.startsWith("$base/forward?q=Z%C3%BCrich%20Hauptbahnhof&"), url)
    }

    @Test
    fun forwardUrlIncludesDefaultParameters() {
        val url = MapboxForwardEndpoint("token").url("Toronto")

        assertTrue(url.contains("permanent"), url)
        assertTrue(url.contains("autocomplete"), url)
    }

    @Test
    fun forwardUrlIncludesConfiguredParameters() {
        val endpoint = MapboxForwardEndpoint("token") {
            country = "ca"
            limit = 2
        }

        val url = endpoint.url("Toronto")

        assertTrue(url.contains("limit"), url)
        assertTrue(url.contains("country"), url)
        assertTrue(url.endsWith("&access_token=token"), url)
    }

    @Test
    fun reverseUrlUsesLatitudeLongitudeAndAccessToken() {
        val url = MapboxReverseEndpoint("token").url(Coordinates(43.65, -79.38))

        assertTrue(url.startsWith("$base/reverse?latitude=43.65&longitude=-79.38&"), url)
        assertTrue(url.endsWith("&access_token=token"), url)
    }

    @Test
    fun reverseUrlWithExplicitParametersIncludesThem() {
        val endpoint = MapboxReverseEndpoint("token", MapboxParameters(language = "fr"))

        val url = endpoint.url(Coordinates(1.0, 2.0))

        assertTrue(url.contains("language"), url)
        assertEquals(1, Regex("access_token").findAll(url).count())
    }
}
