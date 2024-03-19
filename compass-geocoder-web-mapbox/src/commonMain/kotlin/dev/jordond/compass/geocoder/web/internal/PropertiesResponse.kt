package dev.jordond.compass.geocoder.web.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PropertiesResponse(
    @SerialName("coordinates")
    val coordinates: CoordinatesResponse,

    @SerialName("context")
    val context: ContextResponse,
)