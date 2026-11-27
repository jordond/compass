package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsLocationType
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsLocationTypeList
import dev.jordond.compass.geocoder.web.parameter.GoogleMapsParameters
import kotlin.test.Test
import kotlin.test.assertEquals

class GoogleMapsEndpointTest {

    private val base = "https://maps.googleapis.com/maps/api/geocode/json"

    @Test
    fun forwardUrlHasTheAddressAndKey() {
        val url = GoogleMapsForwardEndpoint("secret").url("Toronto")

        assertEquals("$base?address=Toronto&key=secret", url)
    }

    @Test
    fun forwardUrlEncodesTheAddressOnce() {
        val url = GoogleMapsForwardEndpoint("secret").url("1 Main St & Zürich")

        assertEquals("$base?address=1%20Main%20St%20%26%20Z%C3%BCrich&key=secret", url)
    }

    @Test
    fun forwardUrlPutsParametersBetweenTheAddressAndKey() {
        val endpoint = GoogleMapsForwardEndpoint("secret") {
            locationTypes(GoogleMapsLocationType.Rooftop, GoogleMapsLocationType.Approximate)
        }

        val url = endpoint.url("Toronto")

        assertEquals("$base?address=Toronto&location_type=ROOFTOP%7CAPPROXIMATE&key=secret", url)
    }

    @Test
    fun reverseUrlHasTheCoordinatesAndKey() {
        val url = GoogleMapsReverseEndpoint("secret").url(Coordinates(43.65, -79.38))

        assertEquals("$base?latlng=43.65,-79.38&key=secret", url)
    }

    @Test
    fun reverseUrlPutsParametersBetweenTheCoordinatesAndKey() {
        val endpoint = GoogleMapsReverseEndpoint(
            apiKey = "secret",
            parameters = GoogleMapsParameters(
                GoogleMapsLocationTypeList(GoogleMapsLocationType.Approximate),
            ),
        )

        val url = endpoint.url(Coordinates(1.5, 2.5))

        assertEquals("$base?latlng=1.5,2.5&location_type=APPROXIMATE&key=secret", url)
    }
}
