package dev.jordond.compass.autocomplete.web.google.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class StatusResponse {
    @SerialName("OK")
    Ok,

    @SerialName("ZERO_RESULTS")
    ZeroResults,

    @SerialName("OVER_QUERY_LIMIT")
    OverQueryLimit,

    @SerialName("REQUEST_DENIED")
    RequestDenied,

    @SerialName("INVALID_REQUEST")
    InvalidRequest,

    @SerialName("UNKNOWN_ERROR")
    UnknownError,
}