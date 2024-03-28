package dev.jordond.compass.geocoder.web.parameter

import dev.jordond.compass.tools.web.parameter.QueryParamValue

/**
 * The world view parameter is used to filter the results to a specific country or region.
 *
 * See [MapBox Geocoding API](https://docs.mapbox.com/api/search/geocoding-v6/#forward-geocoding-with-search-text-input)
 */
public enum class WorldView : QueryParamValue {
    AR,
    CN,
    IN,
    JP,
    MA,
    RU,
    TR,
    US;

    override val value: String = name.lowercase()
}