package dev.jordond.compass.geocoder.web.opencage.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class ResultResponse(
    @SerialName("components")
    val components: Components,

    @SerialName("formatted")
    val formatted: String,

    @SerialName("geometry")
    val geometry: Geometry,
)

@Serializable
internal class Coordinate(
    @SerialName("lat")
    val lat: Double,

    @SerialName("lng")
    val lng: Double,
)

@Serializable
internal class Components(
    @SerialName("_category")
    val category: String? = null,

    @SerialName("_normalized_city")
    val normalizedCity: String? = null,

    @SerialName("_type")
    val type: String? = null,

    @SerialName("city")
    val city: String? = null,

    @SerialName("continent")
    val continent: String? = null,

    @SerialName("country")
    val country: String? = null,

    @SerialName("country_code")
    val countryCode: String? = null,

    @SerialName("postcode")
    val postcode: String? = null,

    @SerialName("restaurant")
    val restaurant: String? = null,

    @SerialName("road")
    val road: String? = null,

    @SerialName("state")
    val state: String? = null,

    @SerialName("suburb")
    val suburb: String? = null,
)

@Serializable
internal class Geometry(
    @SerialName("lat")
    val lat: Double,

    @SerialName("lng")
    val lng: Double,
)