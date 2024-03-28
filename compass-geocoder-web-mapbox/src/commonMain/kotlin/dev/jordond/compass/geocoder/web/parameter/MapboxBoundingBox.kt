package dev.jordond.compass.geocoder.web.parameter

import dev.drewhamilton.poko.Poko
import dev.jordond.compass.tools.web.parameter.QueryParamValue

/**
 * A bounding box to limit search results to a specific area.
 *
 * See the [Mapbox API documentation](https://docs.mapbox.com/api/search/geocoding-v6/#forward-geocoding-with-structured-input) for more information.
 *
 * @property minLongitude The minimum longitude of the bounding box.
 * @property minLatitude The minimum latitude of the bounding box.
 * @property maxLongitude The maximum longitude of the bounding box.
 * @property maxLatitude The maximum latitude of the bounding box.
 */
@Poko
@Suppress("MemberVisibilityCanBePrivate")
public class MapboxBoundingBox(
    public val minLongitude: Double,
    public val minLatitude: Double,
    public val maxLongitude: Double,
    public val maxLatitude: Double,
) : QueryParamValue {

    public override val value: String = "$minLongitude,$minLatitude,$maxLongitude,$maxLatitude"

    /**
     * Create a copy of [MapboxBoundingBox] with the given or current values.
     *
     * @param minLongitude The minimum longitude of the bounding box.
     * @param minLatitude The minimum latitude of the bounding box.
     * @param maxLongitude The maximum longitude of the bounding box.
     * @param maxLatitude The maximum latitude of the bounding box.
     */
    public fun copy(
        minLongitude: Double = this.minLongitude,
        minLatitude: Double = this.minLatitude,
        maxLongitude: Double = this.maxLongitude,
        maxLatitude: Double = this.maxLatitude,
    ): MapboxBoundingBox = MapboxBoundingBox(minLongitude, minLatitude, maxLongitude, maxLatitude)
}