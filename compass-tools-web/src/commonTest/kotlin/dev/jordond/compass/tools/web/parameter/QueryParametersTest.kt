package dev.jordond.compass.tools.web.parameter

import kotlin.test.Test
import kotlin.test.assertTrue

class QueryParametersTest {

    @Test
    fun shouldFilterOutNullValues() {
        val actual = internalParametersOf(
            "key1" to "value1",
            "key2" to null,
        )

        actual shouldBeEqual mapOf("key1" to "value1")
    }

    @Test
    fun shouldConvertListToCommaSeparatedString() {
        val actual = internalParametersOf(
            "key1" to listOf("value1", "value2"),
            "key2" to listOf(true, false),
        )
        val expected = mapOf(
            "key1" to "value1,value2",
            "key2" to "true,false",
        )
        actual shouldBeEqual expected
    }

    @Test
    fun shouldHandleQueryParamValue() {
        val actual = internalParametersOf(
            "key1" to object : QueryParamValue {
                override val value: String = "foo"
            },
        )
        val expected = mapOf(
            "key1" to "foo",
        )
        actual shouldBeEqual expected
    }

    @Test
    fun shouldEncodeQueryParametersToUrlSafeString() {
        val actual = object : QueryParameters {
            override val parameters: Map<String, String> = mapOf(
                "key1" to "value1",
                "key2" to "value2",
                "key3" to "value with spaces"
            )
        }.encode()
        val expected = "key1=value1&key2=value2&key3=value%20with%20spaces"
        actual shouldBeEqual expected
    }

    @Test
    fun shouldEncodeSpecialCharactersInUrl() {
        val actual = object : QueryParameters {
            override val parameters: Map<String, String> = mapOf(
                "key1" to "value1 with",
                "key2" to "value2/with",
                "key3" to "value3?with",
                "key4" to "value4#with",
                "key5" to "value5&with",
                "key6" to "value6=with",
                "key7" to "value7+with",
                "key8" to "value8%with",
            )
        }.encode()
        val expected = "key1=value1%20with&key2=value2%2Fwith&key3=value3%3Fwith&" +
            "key4=value4%23with&key5=value5%26with&key6=value6%3Dwith&" +
            "key7=value7%2Bwith&key8=value8%25with"
        actual shouldBeEqual expected
    }

    @Test
    fun shouldHandleDefaultQueryListParam() {
        val actual = internalParametersOf(
            "key1" to FooParamList(listOf(FooParam.Bar, FooParam.Baz)),
        )
        val expected = mapOf("key1" to "bar,baz")
        actual shouldBeEqual expected
    }

    @Test
    fun shouldHandleCustomQueryListParamSeparator() {
        val actual = internalParametersOf(
            "key1" to FooParamList(listOf(FooParam.Bar, FooParam.Baz), "|"),
        )
        val expected = mapOf("key1" to "bar|baz")
        actual shouldBeEqual expected
    }

    // TODO: Replace this with kotest once it hits 5.9 for wasm support
    private infix fun <A : Any> A.shouldBeEqual(expected: A): A {
        assertTrue { this == expected }
        return this
    }
}

private enum class FooParam : QueryParamValue {
    Bar,
    Baz;

    override val value: String = name.lowercase()
}

private class FooParamList(
    override val values: List<FooParam>,
    override val separator: String = ",",
) : QueryParamListValue<FooParam>