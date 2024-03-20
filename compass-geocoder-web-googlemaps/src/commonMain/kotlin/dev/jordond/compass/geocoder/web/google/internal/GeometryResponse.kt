package dev.jordond.compass.geocoder.web.google.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GeometryResponse(
    @Serializable
    val location: LocationResponse? = null,
)

@Serializable
internal data class LocationResponse(
    @SerialName("lat")
    val lat: Double,

    @SerialName("lng")
    val lng: Double,
)