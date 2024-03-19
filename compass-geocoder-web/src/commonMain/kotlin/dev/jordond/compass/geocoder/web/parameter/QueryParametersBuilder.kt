package dev.jordond.compass.geocoder.web.parameter

public interface QueryParametersBuilder <T: QueryParameters> {

    public fun build(): T
}