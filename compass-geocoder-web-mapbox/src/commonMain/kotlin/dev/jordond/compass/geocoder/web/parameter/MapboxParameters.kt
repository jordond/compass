package dev.jordond.compass.geocoder.web.parameter

import dev.drewhamilton.poko.Poko
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.tools.web.parameter.QueryParameters
import dev.jordond.compass.tools.web.parameter.QueryParametersBuilder
import dev.jordond.compass.tools.web.parameter.parametersOf

/**
 * Options for the Mapbox geocoder.
 *
 * See the [Mapbox documentation](https://docs.mapbox.com/api/search/geocoding-v6/#forward-geocoding-with-structured-input)
 * for more information.
 *
 * @property limit The maximum number of results to return. The default is 5.
 * @property permanent Specify whether you intend to store the results of the query (true) or not (false, default).
 * @property autocomplete Specify whether to return autocomplete results (true, default) or not
 * (false). When autocomplete is enabled, results will be included that start with the requested
 * string, rather than responses that match it exactly. For example, a query for India might return
 * both India and Indiana with autocomplete enabled, but only India if it’s disabled.
 * @property bbox Limit results to only those contained within the supplied bounding box. Bounding
 * boxes should be supplied as four numbers separated by commas. The bounding box cannot cross the
 * 180th meridian.
 * @property country    Limit results to one or more countries. Permitted values are
 * [ISO 3166 alpha 2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2) country codes separated
 * by commas.
 * @property language Set the language of the text supplied in responses. Also affects result
 * scoring, with results matching the user’s query in the requested language being preferred over
 * results that match in another language. Options are
 * [IETF language tags](https://en.wikipedia.org/wiki/IETF_language_tag) comprised of a mandatory
 * [ISO 639-1 language](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) code.
 * @property proximity Bias the response to favor results that are closer to this location.
 * @property types Filter results to include only a subset (one or more) of the available
 * feature types.
 * @property worldView Returns features that are defined differently by audiences that belong to
 * various regional, cultural, or political groups. If worldview is not set, the us worldview
 * boundaries are returned by default.
 * @constructor Create a new default instance of [MapboxParameters] with API specific defaults.
 */
@Poko
public class MapboxParameters(
    public val permanent: Boolean = false,
    public val autocomplete: Boolean = true,
    public val limit: Int? = null,
    public val bbox: MapboxBoundingBox? = null,
    public val country: String? = null,
    public val language: String? = null,
    public val proximity: String? = null,
    public val types: MapboxTypesList? = null,
    public val worldView: MapboxWorldView? = null,
) : QueryParameters {

    override val parameters: Map<String, String> = parametersOf(
        "permanent" to permanent,
        "autocomplete" to autocomplete,
        "limit" to limit,
        "bbox" to bbox,
        "country" to country,
        "language" to language,
        "proximity" to proximity,
        "types" to types,
        "worldview" to worldView,
    )
}

/**
 * A builder class to build [MapboxParameters].
 */
@Suppress("MemberVisibilityCanBePrivate")
public class MapboxParametersBuilder : QueryParametersBuilder<MapboxParameters> {

    /**
     * @see MapboxParameters.limit
     */
    public var limit: Int = Geocoder.DefaultMaxResults

    /**
     * @see MapboxParameters.permanent
     */
    public var permanent: Boolean = false

    /**
     * @see MapboxParameters.autocomplete
     */
    public var autocomplete: Boolean = true

    /**
     * @see MapboxParameters.bbox
     */
    public var bbox: MapboxBoundingBox? = null

    /**
     * @see MapboxParameters.country
     */
    public var country: String? = null

    /**
     * @see MapboxParameters.language
     */
    public var language: String? = null

    /**
     * @see MapboxParameters.proximity
     */
    public var proximity: String? = null

    /**
     * @see MapboxParameters.types
     */
    public var types: MapboxTypesList? = null

    /**
     * @see MapboxParameters.worldView
     */
    public var worldView: MapboxWorldView? = null

    /**
     * Create a [MapboxBoundingBox] and set it to [bbox].
     * @see MapboxParameters.bbox
     */
    public fun boundingBox(
        minLongitude: Double,
        minLatitude: Double,
        maxLongitude: Double,
        maxLatitude: Double,
    ): MapboxParametersBuilder = apply {
        this.bbox = MapboxBoundingBox(minLongitude, minLatitude, maxLongitude, maxLatitude)
    }

    /**
     * Build the instance of [MapboxParameters].
     */
    override fun build(): MapboxParameters = MapboxParameters(
        limit = limit,
        permanent = permanent,
        autocomplete = autocomplete,
        bbox = bbox,
        country = country,
        language = language,
        proximity = proximity,
        types = types,
        worldView = worldView,
    )
}

/**
 * Create a [MapboxParameters] instance with the provided [block].
 *
 * @param block The builder block to build the instance of [MapboxParameters].
 * @return The instance of [MapboxParameters].
 */
public fun mapboxParameters(block: MapboxParametersBuilder.() -> Unit): MapboxParameters =
    MapboxParametersBuilder().apply(block).build()