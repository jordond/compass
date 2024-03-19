package dev.jordond.compass.geocoder.web.parameters

import dev.jordond.compass.geocoder.web.parameter.QueryParamValue

/**
 * The types of features that can be returned by the MapBox Geocoding API.
 *
 * See [MapBox Geocoding API](https://docs.mapbox.com/api/search/geocoding-v6/#forward-geocoding-with-search-text-input)
 */
public enum class MapBoxTypes : QueryParamValue {
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