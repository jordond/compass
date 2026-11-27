package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.web.parameter.MapboxParameters
import kotlin.test.Test
import kotlin.test.assertEquals

class MapboxEndpointTest {

    private val base = "https://api.mapbox.com/search/geocode/v6"

    private val defaults = "permanent=false&autocomplete=true"

    @Test
    fun forwardUrlHasTheQueryDefaultParametersAndToken() {
        val url = MapboxForwardEndpoint("token").url("Toronto")

        assertEquals("$base/forward?q=Toronto&$defaults&access_token=token", url)
    }

    @Test
    fun forwardUrlEncodesTheQueryOnce() {
        val url = MapboxForwardEndpoint("token").url("1 Main St & Zürich")

        assertEquals(
            "$base/forward?q=1%20Main%20St%20%26%20Z%C3%BCrich&$defaults&access_token=token",
            url,
        )
    }

    @Test
    fun forwardUrlEncodesConfiguredParameterValues() {
        val endpoint = MapboxForwardEndpoint("token") {
            country = "ca,us"
            limit = 2
        }

        val url = endpoint.url("Toronto")

        assertEquals(
            "$base/forward?q=Toronto&$defaults&limit=2&country=ca%2Cus&access_token=token",
            url,
        )
    }

    @Test
    fun reverseUrlHasTheCoordinatesDefaultParametersAndToken() {
        val url = MapboxReverseEndpoint("token").url(Coordinates(43.65, -79.38))

        assertEquals(
            "$base/reverse?latitude=43.65&longitude=-79.38&$defaults&access_token=token",
            url,
        )
    }

    @Test
    fun reverseUrlIncludesExplicitParameters() {
        val endpoint = MapboxReverseEndpoint("token", MapboxParameters(language = "fr"))

        val url = endpoint.url(Coordinates(1.5, 2.5))

        assertEquals(
            "$base/reverse?latitude=1.5&longitude=2.5&$defaults&language=fr&access_token=token",
            url,
        )
    }
}
