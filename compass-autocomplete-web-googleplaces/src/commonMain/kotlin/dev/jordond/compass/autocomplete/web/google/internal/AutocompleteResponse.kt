package dev.jordond.compass.autocomplete.web.google.internal

import dev.jordond.compass.autocomplete.QueryResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AutocompleteResponse(
    @SerialName("predictions")
    val predictions: List<PredictionResponse> = emptyList(),

    @SerialName("status")
    val status: StatusResponse,
)

@Serializable
internal data class PredictionResponse(
    @SerialName("description")
    val description: String,

    @SerialName("place_id")
    val placeId: String,
)

internal fun AutocompleteResponse.toQueryResults(): List<QueryResult> {
    return predictions.map { prediction ->
        QueryResult(
            name = prediction.description,
            id = prediction.placeId,
        )
    }
}