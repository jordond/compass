package dev.jordond.compass.geocoder.web.parameter

/**
 * A filter of one or more location types, separated by a pipe (|). If the parameter contains
 * multiple location types, the API returns all addresses that match any of the types.
 * A note about processing: The location_type parameter does not restrict the search to the
 * specified location types. Rather, the location_type acts as a post-search filter: the API fetches
 * all results for the specified latlng, then discards those results that do not match the specified
 * location types.
 *
 * See [Google Maps Geocoding API](https://developers.google.com/maps/documentation/geocoding/requests-reverse-geocoding#optional_parameters)
 */
public enum class GoogleMapsLocationType : QueryParamValue {

    /**
     * returns only the addresses for which Google has location information accurate down to street
     * address precision.
     */
    Rooftop,

    /**
     * returns only the addresses that reflect an approximation (usually on a road) interpolated
     * between two precise points (such as intersections). An interpolated range generally indicates
     * that rooftop geocodes are unavailable for a street address.
     */
    RangeInterpolated,

    /**
     * returns only geometric centers of a location such as a polyline (for example, a street) or
     * polygon (region).
     */
    GeometricCenter,

    /**
     * returns only the addresses that are characterized as approximate.
     */
    Approximate;

    override val value: String = name.uppercase()
}

/**
 * A list of [GoogleMapsLocationType] to be used as a query parameter.
 */
public class GoogleMapsLocationTypeList(
    override val values: List<GoogleMapsLocationType>,
) : QueryParamListValue<GoogleMapsLocationType> {

    override val separator: String = "|"

    public constructor(vararg values: GoogleMapsLocationType) : this(values.toList())
}