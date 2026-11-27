package dev.jordond.compass.geocoder.web.parameter

import kotlin.test.Test
import kotlin.test.assertEquals

class GoogleMapsParametersTest {

    @Test
    fun defaultParametersAreEmpty() {
        assertEquals(emptyMap(), GoogleMapsParameters().parameters)
        assertEquals(emptyMap(), googleMapsParameters { }.parameters)
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
    fun builderCollectsSingleVarargAndListLocationTypes() {
        val params = googleMapsParameters {
            locationType(GoogleMapsLocationType.Rooftop)
            locationTypes(GoogleMapsLocationType.Approximate)
            locationTypes(listOf(GoogleMapsLocationType.GeometricCenter))
        }

        assertEquals(
            mapOf("location_type" to "ROOFTOP|APPROXIMATE|GEOMETRIC_CENTER"),
            params.parameters,
        )
    }

    @Test
    fun locationTypesUseTheNamesGoogleExpects() {
        assertEquals(
            listOf("ROOFTOP", "RANGE_INTERPOLATED", "GEOMETRIC_CENTER", "APPROXIMATE"),
            GoogleMapsLocationType.entries.map { type -> type.value },
        )
    }
}
