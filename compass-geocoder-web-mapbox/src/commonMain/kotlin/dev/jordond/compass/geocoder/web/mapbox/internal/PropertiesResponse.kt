package dev.jordond.compass.geocoder.web.mapbox.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PropertiesResponse(
    @SerialName("name")
    val name: String? = null,

    @SerialName("coordinates")
    val coordinates: CoordinatesResponse? = null,

    @SerialName("context")
    val context: ContextResponse? = null,
)