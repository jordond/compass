package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import kotlin.test.Test
import kotlin.test.assertTrue

class OpenCageEndpointTest {

    private val base = "https://api.opencagedata.com/geocode/v1/json"

    @Test
    fun forwardUrlUsesBaseUrlQueryAndKey() {
        val url = OpenCageForwardEndpoint("secret").url("Toronto")

        assertTrue(url.startsWith("$base?q=Toronto&"), url)
        assertTrue(url.endsWith("&key=secret"), url)
    }

    @Test
    fun forwardUrlEncodesSpacesAndNonAsciiCharacters() {
        val url = OpenCageForwardEndpoint("secret").url("Zürich Hauptbahnhof")

        assertTrue(url.startsWith("$base?q=Z%C3%BCrich%20Hauptbahnhof&"), url)
    }

    @Test
    fun forwardUrlIncludesDefaultParameters() {
        val url = OpenCageForwardEndpoint("secret").url("Toronto")

        assertTrue(url.contains("limit"), url)
        assertTrue(url.contains("dedupe"), url)
        assertTrue(url.contains("record"), url)
    }

    @Test
    fun forwardUrlIncludesConfiguredCountryCodes() {
        val endpoint = OpenCageForwardEndpoint("secret") { countryCode("ca", "us") }

        val url = endpoint.url("Toronto")

        assertTrue(url.contains("countrycode"), url)
        assertTrue(url.endsWith("&key=secret"), url)
    }

    @Test
    fun reverseUrlJoinsLatitudeAndLongitudeWithPlus() {
        val url = OpenCageReverseEndpoint("secret").url(Coordinates(43.65, -79.38))

        assertTrue(url.startsWith("$base?q=43.65+-79.38&"), url)
        assertTrue(url.endsWith("&key=secret"), url)
    }
}
