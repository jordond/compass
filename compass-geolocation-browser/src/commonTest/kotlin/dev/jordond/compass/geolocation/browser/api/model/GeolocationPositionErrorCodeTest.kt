package dev.jordond.compass.geolocation.browser.api.model

import kotlin.test.Test
import kotlin.test.assertEquals

class GeolocationPositionErrorCodeTest {

    @Test
    fun codesMatchTheW3cGeolocationSpec() {
        // The browser reports these numbers, and an error is mapped to an entry by its code alone
        assertEquals(
            mapOf(
                GeolocationPositionErrorCode.PermissionDenied to 1,
                GeolocationPositionErrorCode.PositionUnavailable to 2,
                GeolocationPositionErrorCode.Timeout to 3,
            ),
            GeolocationPositionErrorCode.entries.associateWith { entry -> entry.code },
        )
    }
}
