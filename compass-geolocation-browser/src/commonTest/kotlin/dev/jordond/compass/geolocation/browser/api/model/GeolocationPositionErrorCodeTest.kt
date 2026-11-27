package dev.jordond.compass.geolocation.browser.api.model

import kotlin.test.Test
import kotlin.test.assertEquals

class GeolocationPositionErrorCodeTest {

    @Test
    fun codesMatchTheW3cGeolocationSpec() {
        assertEquals(1, GeolocationPositionErrorCode.PermissionDenied.code)
        assertEquals(2, GeolocationPositionErrorCode.PositionUnavailable.code)
        assertEquals(3, GeolocationPositionErrorCode.Timeout.code)
    }

    @Test
    fun everySpecCodeHasExactlyOneEntry() {
        assertEquals(
            listOf(1, 2, 3),
            GeolocationPositionErrorCode.entries.map { it.code }.sorted(),
        )
    }
}
