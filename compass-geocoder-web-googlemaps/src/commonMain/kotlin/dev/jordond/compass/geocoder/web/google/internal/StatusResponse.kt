package dev.jordond.compass.geocoder.web.google.internal

import dev.jordond.compass.InternalCompassApi
import kotlinx.serialization.SerialName

/**
 * The possible status responses from the Google Maps Geocoding API.
 *
 * @see [Status and Error Codes](https://developers.google.com/maps/documentation/geocoding/requests-reverse-geocoding#reverse-response)
 */
@Suppress("unused")
@InternalCompassApi
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