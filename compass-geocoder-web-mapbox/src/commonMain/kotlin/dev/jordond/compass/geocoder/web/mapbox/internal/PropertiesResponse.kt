package dev.jordond.compass.geocoder.web.mapbox.internal

import dev.jordond.compass.InternalCompassApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalCompassApi
@Serializable
public data class PropertiesResponse(
    @SerialName("name")
    val name: String? = null,

    @SerialName("coordinates")
    val coordinates: CoordinatesResponse? = null,

    @SerialName("context")
    val context: ContextResponse? = null,
)