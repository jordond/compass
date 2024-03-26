package dev.jordond.compass.geocoder.web.parameter

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
        val expected = "key1%3Dvalue1%26key2%3Dvalue2%26key3%3Dvalue%20with%20spaces"
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
        val expected = "key1%3Dvalue1%20with%26key2%3Dvalue2%2Fwith%26key3%3Dvalue3%3Fwith%26" +
            "key4%3Dvalue4%23with%26key5%3Dvalue5%26with%26key6%3Dvalue6%3Dwith%26" +
            "key7%3Dvalue7%2Bwith%26key8%3Dvalue8%25with"
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