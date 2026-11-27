package dev.jordond.compass.geocoder.web.parameter

import kotlin.test.Test
import kotlin.test.assertEquals

class OpenCageParametersTest {

    @Test
    fun defaultsSendLimitOfTenAndDisabledFlagsAndNoCountryCode() {
        val params = OpenCageParameters()

        assertEquals(
            mapOf("limit" to "10", "no_dedupe" to "0", "no_record" to "0"),
            params.parameters,
        )
    }

    @Test
    fun allParametersAreSerialized() {
        val params = OpenCageParameters(
            limit = 3,
            countryCode = listOf("ca", "us"),
            noDedupe = true,
            noRecord = true,
        )

        assertEquals(
            mapOf(
                "limit" to "3",
                "countrycode" to "ca,us",
                "no_dedupe" to "1",
                "no_record" to "1",
            ),
            params.parameters,
        )
    }

    @Test
    fun builderDefaultsMatchParameterDefaults() {
        assertEquals(OpenCageParameters().parameters, openCageParameters { }.parameters)
    }

    @Test
    fun builderPassesEveryValueThrough() {
        val params = openCageParameters {
            limit = 1
            countryCode("ca", "mx")
            noDedupe = true
            noRecord = true
        }

        assertEquals(
            mapOf("limit" to "1", "countrycode" to "ca,mx", "no_dedupe" to "1", "no_record" to "1"),
            params.parameters,
        )
    }

    @Test
    fun builderListCountryCodesReplacePreviousOnes() {
        val params = openCageParameters {
            countryCode("ca")
            countryCodes(listOf("fr", "de"))
        }

        assertEquals(listOf("fr", "de"), params.countryCode)
    }
}
