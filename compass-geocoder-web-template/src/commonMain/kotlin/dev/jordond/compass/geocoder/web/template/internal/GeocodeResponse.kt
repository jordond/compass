package dev.jordond.compass.geocoder.web.template.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GeocodeResponse(
    @SerialName("results")
    val results: List<ResultResponse> = emptyList(),
)

@Serializable
internal data class ResultResponse(
    @SerialName("foo")
    val foo: String? = null,
)

internal fun GeocodeResponse.toCoordinates(): List<Coordinates> {
    return results.mapNotNull {
        // TODO: Implement this
        null
    }
}

internal fun GeocodeResponse.toPlaces(): List<Place> {
    return results.mapNotNull {
        // TODO: Implement this
        null
    }
}