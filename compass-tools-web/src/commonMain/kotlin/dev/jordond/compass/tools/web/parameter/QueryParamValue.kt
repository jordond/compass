package dev.jordond.compass.tools.web.parameter

/**
 * Represents a query parameter value.
 *
 * Encoded by [QueryParameters] to make a URL safe string.
 */
public interface QueryParamValue {

    /**
     * The value of the query parameter.
     */
    public val value: String
}