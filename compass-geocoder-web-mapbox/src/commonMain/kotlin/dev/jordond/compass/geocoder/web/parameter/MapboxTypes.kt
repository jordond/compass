package dev.jordond.compass.geocoder.web.parameter

import dev.jordond.compass.tools.web.parameter.QueryParamListValue
import dev.jordond.compass.tools.web.parameter.QueryParamValue

/**
 * The types of features that can be returned by the Mapbox Geocoding API.
 *
 * See [Mapbox Geocoding API](https://docs.mapbox.com/api/search/geocoding-v6/#forward-geocoding-with-search-text-input)
 */
public enum class MapboxTypes : QueryParamValue {
    Country,
    Region,
    Postcode,
    District,
    Place,
    Locality,
    Neighborhood,
    Street,
    Address;

    override val value: String = name.lowercase()
}

/**
 * A list of [MapboxTypes] to be used as a query parameter.
 */
public class MapboxTypesList(
    override val values: List<MapboxTypes>,
) : QueryParamListValue<MapboxTypes> {

    public constructor(vararg values: MapboxTypes) : this(values.toList())
}