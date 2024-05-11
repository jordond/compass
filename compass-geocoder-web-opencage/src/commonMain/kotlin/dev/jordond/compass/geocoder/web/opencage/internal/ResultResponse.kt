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
    val category: String,

    @SerialName("_normalized_city")
    val normalizedCity: String,

    @SerialName("_type")
    val type: String,

    @SerialName("city")
    val city: String,

    @SerialName("continent")
    val continent: String,

    @SerialName("country")
    val country: String,

    @SerialName("country_code")
    val countryCode: String,

    @SerialName("postcode")
    val postcode: String,

    @SerialName("restaurant")
    val restaurant: String,

    @SerialName("road")
    val road: String,

    @SerialName("state")
    val state: String,

    @SerialName("suburb")
    val suburb: String,
)

@Serializable
internal class Geometry(
    @SerialName("lat")
    val lat: Double,

    @SerialName("lng")
    val lng: Double,
)