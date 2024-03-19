package dev.jordond.compass.geocoder.web.parameter

import io.ktor.http.encodeURLQueryComponent

/**
 * Represents a set of query parameters for a URL request.
 */
public interface QueryParameters {

    /**
     * Key-value pairs of query parameters.
     */
    public val parameters: Map<String, String>

    /**
     * Encodes the query parameters into a URL-encoded string.
     *
     * @return The URL-encoded query parameters.
     */
    public fun encode(): String = parameters
        .map { (key, value) -> "$key=$value" }
        .joinToString("&")
        .encodeURLQueryComponent(encodeFull = true)
}

/**
 * Create a query parameter map from the given pairs.
 *
 * @param parameters The key-value pairs of query parameters.
 * @return The query parameter map.
 */
@Suppress("UnusedReceiverParameter")
public fun QueryParameters.parametersOf(
    vararg parameters: Pair<String, Any?>,
): Map<String, String> = internalParametersOf(*parameters)

internal fun internalParametersOf(
    vararg parameters: Pair<String, Any?>,
): Map<String, String> = parameters
    .mapNotNull { (key, value) ->
        value?.let { nonNullValue ->
            when (nonNullValue) {
                is QueryParamValue -> key to nonNullValue.value
                is List<*> -> {
                    key to nonNullValue
                        .map { if (it is QueryParamValue) it.value else it.toString() }
                        .toQueryParameterValue()
                }
                else -> key to nonNullValue.toString()
            }
        }
    }
    .toMap()

private fun <E> List<E>.toQueryParameterValue(): String = joinToString(",")