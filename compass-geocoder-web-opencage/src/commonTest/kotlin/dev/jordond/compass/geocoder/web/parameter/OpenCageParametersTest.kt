package dev.jordond.compass.geocoder.web.parameter

import kotlin.test.Test
import kotlin.test.assertEquals

class OpenCageParametersTest {

    @Test
    fun defaultsSendLimitOfTenAndDisabledFlags() {
        val params = OpenCageParameters()

        assertEquals(
            mapOf("limit" to "10", "countrycode" to "", "no_dedupe" to "0", "no_record" to "0"),
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
    fun builderVarargCountryCodes() {
        val params = openCageParameters { countryCode("ca", "mx") }

        assertEquals("ca,mx", params.parameters["countrycode"])
    }

    @Test
    fun builderListCountryCodesReplacePreviousOnes() {
        val params = openCageParameters {
            countryCode("ca")
            countryCodes(listOf("fr", "de"))
        }

        assertEquals(listOf("fr", "de"), params.countryCode)
    }

    @Test
    fun builderPassesEveryValueThrough() {
        val params = openCageParameters {
            limit = 1
            noDedupe = true
            noRecord = true
        }

        assertEquals(1, params.limit)
        assertEquals("1", params.parameters["no_dedupe"])
        assertEquals("1", params.parameters["no_record"])
    }
}
