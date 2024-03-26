package dev.jordond.compass.geocoder.web.parameter

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

/**
 * A list of [MapBoxTypes] to be used as a query parameter.
 */
public class MapBoxTypesList(
    override val values: List<MapBoxTypes>,
) : QueryParamListValue<MapBoxTypes> {

    public constructor(vararg values: MapBoxTypes) : this(values.toList())
}