package dev.jordond.compass.geocoder.web.parameter

import dev.drewhamilton.poko.Poko

/**
 * Options for the Google Maps geocoder.
 *
 * See the [Google Maps documentation](https://developers.google.com/maps/documentation/geocoding/requests-geocoding)
 * for more information.
 *
 * @param locationType A filter of one or more location types, separated by a pipe (|). If the
 * parameter contains multiple location types, the API returns all addresses that match any of the
 * types. A note about processing: The location_type parameter does not restrict the search to the
 * specified location types. Rather, the location_type acts as a post-search filter: the API fetches
 * all results for the specified latlng, then discards those results that do not match the specified
 * location types.
 */
@Poko
public class GoogleMapsParameters(
    public val locationType: GoogleMapsLocationTypeList? = null,
) : QueryParameters {

    override val parameters: Map<String, String> = parametersOf(
        "location_type" to locationType,
    )
}

/**
 * A builder class to build [GoogleMapsParameters].
 */
@Suppress("MemberVisibilityCanBePrivate")
public class GoogleMapsParametersBuilder : QueryParametersBuilder<GoogleMapsParameters> {

    /**
     * @see GoogleMapsParameters.locationType
     */
    private val locationType: MutableList<GoogleMapsLocationType> = mutableListOf()

    /**
     * Add a location type to the list of location types.
     */
    public fun locationType(location: GoogleMapsLocationType) {
        locationType.add(location)
    }

    /**
     * Add multiple location types to the list of location types.
     */
    public fun locationTypes(vararg locations: GoogleMapsLocationType) {
        locationType.addAll(locations)
    }

    /**
     * Add multiple location types to the list of location types.
     */
    public fun locationTypes(locations: List<GoogleMapsLocationType>) {
        locationType.addAll(locations)
    }

    /**
     * Build the instance of [GoogleMapsParameters].
     */
    override fun build(): GoogleMapsParameters = GoogleMapsParameters(
        locationType = if (locationType.isEmpty()) null
        else GoogleMapsLocationTypeList(locationType.toList()),
    )
}

/**
 * Create a [GoogleMapsParameters] instance with the provided [block].
 *
 * @param block The builder block to build the instance of [GoogleMapsParameters].
 * @return The instance of [GoogleMapsParameters].
 */
public fun googleMapsParameters(
    block: GoogleMapsParametersBuilder.() -> Unit,
): GoogleMapsParameters = GoogleMapsParametersBuilder().apply(block).build()