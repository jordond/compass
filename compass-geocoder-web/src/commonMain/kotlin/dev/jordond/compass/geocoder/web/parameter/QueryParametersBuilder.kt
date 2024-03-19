package dev.jordond.compass.geocoder.web.parameter

/**
 * Denotes a class that can build a [QueryParameters] instance of type [T].
 *
 * @param T the type of [QueryParameters] to build.
 */
public interface QueryParametersBuilder <T: QueryParameters> {

    /**
     * @return a [QueryParameters] instance of type [T].
     */
    public fun build(): T
}