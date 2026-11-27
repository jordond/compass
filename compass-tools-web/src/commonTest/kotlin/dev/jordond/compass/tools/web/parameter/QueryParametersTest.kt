package dev.jordond.compass.tools.web.parameter

import kotlin.test.Test
import kotlin.test.assertEquals

class QueryParametersTest {

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
    fun parametersOfDropsNullValues() {
        val actual = internalParametersOf("key1" to "value1", "key2" to null)

        assertEquals(mapOf("key1" to "value1"), actual)
    }

    @Test
    fun parametersOfConvertsEachKindOfValue() {
        val actual = Params(emptyMap()).parametersOf(
            "kind" to Kind.A,
            "kinds" to Kinds(listOf(Kind.A, Kind.B)),
            "piped" to Kinds(listOf(Kind.A, Kind.B), separator = "|"),
            "list" to listOf(Kind.B, Kind.A),
            "plainList" to listOf(true, false),
            "number" to 5,
        )

        assertEquals(
            mapOf(
                "kind" to "a",
                "kinds" to "a,b",
                "piped" to "a|b",
                "list" to "b,a",
                "plainList" to "true,false",
                "number" to "5",
            ),
            actual,
        )
    }

    @Test
    fun emptyListValueIsAnEmptyString() {
        assertEquals("", Kinds(emptyList()).value)
    }

    @Test
    fun encodeJoinsPairsWithAmpersandsAndKeepsOrder() {
        val params = Params(mapOf("key1" to "value1", "key2" to "value2"))

        assertEquals("key1=value1&key2=value2", params.encode())
    }

    @Test
    fun encodeEscapesReservedCharactersInKeysAndValues() {
        val params = Params(
            mapOf(
                "space" to "a b",
                "slash" to "a/b",
                "question" to "a?b",
                "hash" to "a#b",
                "ampersand" to "a&b",
                "equals" to "a=b",
                "plus" to "a+b",
                "percent" to "a%b",
                "comma" to "a,b",
                "unicode" to "Zürich",
                "a key&more" to "x",
            ),
        )

        assertEquals(
            "space=a%20b&slash=a%2Fb&question=a%3Fb&hash=a%23b&ampersand=a%26b&equals=a%3Db&" +
                "plus=a%2Bb&percent=a%25b&comma=a%2Cb&unicode=Z%C3%BCrich&a%20key%26more=x",
            params.encode(),
        )
    }

    @Test
    fun encodeOfNoParametersIsEmpty() {
        assertEquals("", Params(emptyMap()).encode())
    }

    @Test
    fun booleanToIntMapsTrueToOneAndFalseToZero() {
        with(Params(emptyMap())) {
            assertEquals(1, true.toInt())
            assertEquals(0, false.toInt())
        }
    }
}
