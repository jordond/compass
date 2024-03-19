package dev.jordond.compass.geocoder.web.mapbox.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class FeatureResponse(
    @SerialName("properties")
    val properties: PropertiesResponse? = null,
)