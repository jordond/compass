package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenCageEndpointTest {

    private val base = "https://api.opencagedata.com/geocode/v1/json"

    private val defaults = "limit=10&no_dedupe=0&no_record=0"

    @Test
    fun forwardUrlHasTheQueryDefaultParametersAndKey() {
        val url = OpenCageForwardEndpoint("secret").url("Toronto")

        assertEquals("$base?q=Toronto&$defaults&key=secret", url)
    }

    @Test
    fun forwardUrlEncodesTheQueryOnce() {
        val url = OpenCageForwardEndpoint("secret").url("1 Main St & Zürich")

        assertEquals("$base?q=1%20Main%20St%20%26%20Z%C3%BCrich&$defaults&key=secret", url)
    }

    @Test
    fun forwardUrlEncodesConfiguredCountryCodes() {
        val endpoint = OpenCageForwardEndpoint("secret") { countryCode("ca", "us") }

        val url = endpoint.url("Toronto")

        assertEquals(
            "$base?q=Toronto&limit=10&countrycode=ca%2Cus&no_dedupe=0&no_record=0&key=secret",
            url,
        )
    }

    @Test
    fun reverseUrlJoinsLatitudeAndLongitudeWithPlus() {
        val url = OpenCageReverseEndpoint("secret").url(Coordinates(43.65, -79.38))

        assertEquals("$base?q=43.65+-79.38&$defaults&key=secret", url)
    }
}
