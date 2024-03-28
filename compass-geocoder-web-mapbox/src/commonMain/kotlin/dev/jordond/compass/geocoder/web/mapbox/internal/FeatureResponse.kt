package dev.jordond.compass.geocoder.web.mapbox.internal

import dev.jordond.compass.InternalCompassApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalCompassApi
@Serializable
public data class FeatureResponse(
    @SerialName("properties")
    val properties: PropertiesResponse? = null,
)