package dev.jordond.compass.geocoder.web.parameters

import dev.drewhamilton.poko.Poko
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.web.parameter.QueryParameters
import dev.jordond.compass.geocoder.web.parameter.QueryParametersBuilder
import dev.jordond.compass.geocoder.web.parameter.parametersOf

/**
 * Options for the MapBox geocoder.
 *
 * See the [MapBox documentation](https://docs.mapbox.com/api/search/geocoding-v6/#forward-geocoding-with-structured-input)
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
 */
@Poko
public class MapBoxParameters(
    public val limit: Int = Geocoder.DefaultMaxResults,
    public val permanent: Boolean = false,
    public val autocomplete: Boolean = true,
    public val bbox: BoundingBox? = null,
    public val country: String? = null,
    public val language: String? = null,
    public val proximity: String? = null,
    public val types: List<MapBoxTypes>? = null,
    public val worldView: WorldView? = null,
) : QueryParameters {

    override val parameters: Map<String, String> = parametersOf(
        "limit" to limit,
        "permanent" to permanent,
        "autocomplete" to autocomplete,
        "bbox" to bbox,
        "country" to country,
        "language" to language,
        "proximity" to proximity,
        "types" to types,
        "worldview" to worldView,
    )
}

/**
 * A builder class to build [MapBoxParameters].
 */
@Suppress("MemberVisibilityCanBePrivate")
public class MapBoxParametersBuilder : QueryParametersBuilder<MapBoxParameters> {

    /**
     * @see MapBoxParameters.limit
     */
    public var limit: Int = Geocoder.DefaultMaxResults

    /**
     * @see MapBoxParameters.permanent
     */
    public var permanent: Boolean = false

    /**
     * @see MapBoxParameters.autocomplete
     */
    public var autocomplete: Boolean = true

    /**
     * @see MapBoxParameters.bbox
     */
    public var bbox: BoundingBox? = null

    /**
     * @see MapBoxParameters.country
     */
    public var country: String? = null

    /**
     * @see MapBoxParameters.language
     */
    public var language: String? = null

    /**
     * @see MapBoxParameters.proximity
     */
    public var proximity: String? = null

    /**
     * @see MapBoxParameters.types
     */
    public var types: List<MapBoxTypes>? = null

    /**
     * @see MapBoxParameters.worldView
     */
    public var worldView: WorldView? = null

    /**
     * Create a [BoundingBox] and set it to [bbox].
     * @see MapBoxParameters.bbox
     */
    public fun boundingBox(
        minLongitude: Double,
        minLatitude: Double,
        maxLongitude: Double,
        maxLatitude: Double,
    ): MapBoxParametersBuilder = apply {
        this.bbox = BoundingBox(minLongitude, minLatitude, maxLongitude, maxLatitude)
    }

    /**
     * Build the instance of [MapBoxParameters].
     */
    override fun build(): MapBoxParameters = MapBoxParameters(
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
 * Create a [MapBoxParameters] instance with the provided [block].
 *
 * @param block The builder block to build the instance of [MapBoxParameters].
 * @return The instance of [MapBoxParameters].
 */
public fun mapBoxParameters(block: MapBoxParametersBuilder.() -> Unit): MapBoxParameters =
    MapBoxParametersBuilder().apply(block).build()