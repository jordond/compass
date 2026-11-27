package dev.jordond.compass.geocoder.web.parameter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GoogleMapsParametersTest {

    @Test
    fun defaultParametersAreEmpty() {
        val params = GoogleMapsParameters()

        assertNull(params.locationType)
        assertEquals(emptyMap(), params.parameters)
    }

    @Test
    fun locationTypesAreJoinedWithPipe() {
        val params = GoogleMapsParameters(
            GoogleMapsLocationTypeList(
                GoogleMapsLocationType.Rooftop,
                GoogleMapsLocationType.Approximate,
            ),
        )

        assertEquals(mapOf("location_type" to "ROOFTOP|APPROXIMATE"), params.parameters)
    }

    @Test
    fun builderWithoutLocationTypesProducesNoParameter() {
        val params = googleMapsParameters { }

        assertNull(params.locationType)
        assertEquals(emptyMap(), params.parameters)
    }

    @Test
    fun builderCollectsSingleVarargAndListLocationTypes() {
        val params = googleMapsParameters {
            locationType(GoogleMapsLocationType.Rooftop)
            locationTypes(GoogleMapsLocationType.Approximate)
            locationTypes(listOf(GoogleMapsLocationType.Rooftop))
        }

        assertEquals(
            mapOf("location_type" to "ROOFTOP|APPROXIMATE|ROOFTOP"),
            params.parameters,
        )
    }

    @Test
    fun parametersWithSameLocationTypesAreEqual() {
        val first = GoogleMapsParameters(GoogleMapsLocationTypeList(GoogleMapsLocationType.Rooftop))
        val second = googleMapsParameters { locationType(GoogleMapsLocationType.Rooftop) }

        assertEquals(first.parameters, second.parameters)
    }
}
