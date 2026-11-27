package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import kotlin.test.Test
import kotlin.test.assertEquals

class TemplateEndpointTest {

    @Test
    fun forwardUrlContainsEncodedAddressAndKey() {
        val url = TemplateForwardEndpoint("secret").url("New York")

        assertEquals("?address=New%20York&key=secret", url)
    }

    @Test
    fun reverseUrlContainsLatLngAndKey() {
        val url = TemplateReverseEndpoint("secret").url(Coordinates(43.65, -79.38))

        assertEquals("?latlng=43.65,-79.38&key=secret", url)
    }
}
