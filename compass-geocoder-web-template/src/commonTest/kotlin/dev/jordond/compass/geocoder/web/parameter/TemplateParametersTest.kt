package dev.jordond.compass.geocoder.web.parameter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TemplateParametersTest {

    @Test
    fun defaultParametersAreEmpty() {
        val params = TemplateParameters()

        assertNull(params.foo)
        assertEquals(emptyMap(), params.parameters)
    }

    @Test
    fun fooIsSerialized() {
        assertEquals(mapOf("foo" to "bar"), TemplateParameters(foo = "bar").parameters)
    }

    @Test
    fun builderWithoutValuesMatchesDefaults() {
        assertEquals(TemplateParameters().parameters, templateParameters { }.parameters)
    }
}
