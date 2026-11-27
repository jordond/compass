package dev.jordond.compass.geolocation

import dev.jordond.compass.test.location
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class GeolocatorResultTest {

    @Test
    fun successExposesItsLocationAndOnlyRunsOnSuccess() {
        val result: GeolocatorResult = GeolocatorResult.Success(location())
        var succeeded = 0
        var failed = 0

        assertFalse(result.isError)
        assertEquals(location(), result.getOrNull())
        assertNull(result.errorOrNull())
        result.onSuccess { succeeded++ }.onFailed { failed++ }
        assertEquals(1 to 0, succeeded to failed)
    }

    @Test
    fun errorsExposeThemselvesAndOnlyRunOnFailed() {
        val errors = listOf(
            GeolocatorResult.NotFound,
            GeolocatorResult.NotSupported,
            GeolocatorResult.GeolocationFailed("boom"),
            GeolocatorResult.PermissionDenied(false),
        )

        errors.forEach { error ->
            var succeeded = 0
            var failed = 0

            assertTrue(error.isError, "$error")
            assertNull(error.getOrNull(), "$error")
            assertSame(error, error.errorOrNull(), "$error")
            error.onSuccess { succeeded++ }.onFailed { failed++ }
            assertEquals(0 to 1, succeeded to failed, "$error")
        }
    }
}
