package dev.jordond.compass.tools.web.parameter

/**
 * Denotes a query parameter value that is a list of values.
 *
 * @param T The type of the list elements.
 * @property separator The separator to use when joining the list elements into a single string.
 * @property values The list of values.
 */
public interface QueryParamListValue<T : QueryParamValue> : QueryParamValue {

    public val separator: String
        get() = ","

    public val values: List<T>

    override val value: String
        get() = values.joinToString(separator) { it.value }
}
