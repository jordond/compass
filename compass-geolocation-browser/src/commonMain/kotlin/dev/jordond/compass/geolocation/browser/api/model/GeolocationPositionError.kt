package dev.jordond.compass.geolocation.browser.api.model

/**
 * Represents an error that occurred while retrieving the geolocation.
 *
 * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/GeolocationPositionError)
 *
 * @property code The error code.
 * @property message A human-readable message describing the error.
 */
public external class GeolocationPositionError {

    /**
     * The error code.
     *
     * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/GeolocationPositionError/code)
     *
     * The following values are supported:
     * - 1: Permission denied
     * - 2: Position unavailable
     * - 3: Timeout
     */
    public var code: Int

    /**
     * A human-readable message describing the error.
     *
     * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/GeolocationPositionError/message)
     */
    public var message: String
}

internal enum class GeolocationPositionErrorCode(val code: Int) {
    PermissionDenied(1),
    PositionUnavailable(2),
    Timeout(3)
}

internal fun GeolocationPositionError.value(): GeolocationPositionErrorCode =
    GeolocationPositionErrorCode.entries.first { it.code == code }
