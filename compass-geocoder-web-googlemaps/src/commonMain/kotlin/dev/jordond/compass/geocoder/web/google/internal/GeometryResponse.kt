package dev.jordond.compass.geocoder.web.google.internal

import dev.jordond.compass.InternalCompassApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalCompassApi
@Serializable
public data class GeometryResponse(
    @Serializable
    val location: LocationResponse? = null,
)

@InternalCompassApi
@Serializable
public data class LocationResponse(
    @SerialName("lat")
    val lat: Double,

    @SerialName("lng")
    val lng: Double,
)