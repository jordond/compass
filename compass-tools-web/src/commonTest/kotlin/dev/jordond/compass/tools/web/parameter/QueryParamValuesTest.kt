package dev.jordond.compass.tools.web.parameter

import kotlin.test.Test
import kotlin.test.assertEquals

class QueryParamValuesTest {

    private enum class Kind(override val value: String) : QueryParamValue {
        A("a"),
        B("b"),
    }

    private class Kinds(
        override val values: List<Kind>,
        override val separator: String = ",",
    ) : QueryParamListValue<Kind>

    private class Params(override val parameters: Map<String, String>) : QueryParameters

    @Test
    fun listValueJoinsWithDefaultSeparator() {
        assertEquals("a,b", Kinds(listOf(Kind.A, Kind.B)).value)
    }

    @Test
    fun listValueUsesCustomSeparator() {
        assertEquals("a|b", Kinds(listOf(Kind.A, Kind.B), separator = "|").value)
    }

    @Test
    fun emptyListValueIsEmptyString() {
        assertEquals("", Kinds(emptyList()).value)
    }

    @Test
    fun parametersOfUsesQueryParamValueAndListValue() {
        val params = Params(emptyMap())
        val result = params.parametersOf(
            "kind" to Kind.A,
            "kinds" to Kinds(listOf(Kind.A, Kind.B), "|"),
            "list" to listOf(Kind.B, Kind.A),
            "plain" to 5,
            "skip" to null,
        )
        assertEquals(
            mapOf("kind" to "a", "kinds" to "a|b", "list" to "b,a", "plain" to "5"),
            result,
        )
    }

    @Test
    fun encodeEmptyParametersIsEmpty() {
        assertEquals("", Params(emptyMap()).encode())
    }

    @Test
    fun booleanToIntMapsTrueToOne() {
        with(Params(emptyMap())) {
            assertEquals(1, true.toInt())
            assertEquals(0, false.toInt())
        }
    }
}
