package dev.jordond.compass.autocomplete.web.google.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DetailsResponse(
    @SerialName("status")
    val status: StatusResponse,
)