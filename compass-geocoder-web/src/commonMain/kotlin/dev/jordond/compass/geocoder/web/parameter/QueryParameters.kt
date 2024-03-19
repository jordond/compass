package dev.jordond.compass.geocoder.web.parameter

import io.ktor.http.encodeURLQueryComponent

public interface QueryParameters {

    public val parameters: Map<String, String>

    public fun encode(): String = parameters
        .map { (key, value) -> "$key=$value" }
        .joinToString("&")
        .encodeURLQueryComponent(encodeFull = true)
}

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
                is List<*> -> key to nonNullValue.toQueryParameterValue()
                else -> key to nonNullValue.toString()
            }
        }
    }
    .toMap()

private fun <E> List<E>.toQueryParameterValue(): String = joinToString(",")